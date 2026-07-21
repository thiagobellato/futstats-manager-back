package br.com.bellato.gerenciador_fifa.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartidaEvento;
import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;
import br.com.bellato.gerenciador_fifa.model.CampeonatoSuspensao;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoSuspensaoRepository;
import br.com.bellato.gerenciador_fifa.service.transferencia.CampeonatoAtletaIdentidade;

/**
 * Suspensão automática por cartão vermelho — exclusiva do campeonato.
 * Segue a identidade do atleta (sobrevive a transferências internas).
 */
@Service
public class CampeonatoSuspensaoService {

    public static final String MOTIVO_EXPULSAO = "Suspenso por expulsão";

    @Autowired
    private CampeonatoSuspensaoRepository campeonatoSuspensaoRepository;

    @Transactional(readOnly = true)
    public Set<String> identidadesSuspensasAtivas(Long campeonatoId) {
        return campeonatoSuspensaoRepository.findByCampeonatoCampeonatoIdAndAtivaTrue(campeonatoId).stream()
                .map(CampeonatoSuspensao::getIdentidade)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Recalcula suspensões a partir das partidas finalizadas (idempotente).
     * Ordem: rodada → ordem da partida. Transferências aplicadas por id do vínculo
     * entre partidas, preservando a regra de “próxima partida do clube atual”.
     */
    @Transactional
    public void recalcularSuspensoes(
            Campeonato campeonato,
            List<CampeonatoAtleta> atletas) {

        Long campeonatoId = campeonato.getCampeonatoId();
        campeonatoSuspensaoRepository.deleteByCampeonatoCampeonatoId(campeonatoId);

        Map<String, Long> clubeAtualPorIdentidade = new HashMap<>();
        List<TransferenciaTimeline> transferencias = new ArrayList<>();

        Map<String, List<CampeonatoAtleta>> porIdentidade = new HashMap<>();
        for (CampeonatoAtleta atleta : atletas) {
            String identidade = CampeonatoAtletaIdentidade.garantir(atleta);
            porIdentidade.computeIfAbsent(identidade, k -> new ArrayList<>()).add(atleta);
        }

        for (Map.Entry<String, List<CampeonatoAtleta>> entry : porIdentidade.entrySet()) {
            List<CampeonatoAtleta> vinculos = entry.getValue().stream()
                    .sorted(Comparator.comparing(a -> a.getCampeonatoAtletaId() == null ? 0L : a.getCampeonatoAtletaId()))
                    .collect(Collectors.toList());
            if (vinculos.isEmpty()) {
                continue;
            }
            CampeonatoAtleta primeiro = vinculos.get(0);
            if (primeiro.getCampeonatoClube() != null) {
                clubeAtualPorIdentidade.put(entry.getKey(), primeiro.getCampeonatoClube().getCampeonatoClubeId());
            }
            for (int i = 1; i < vinculos.size(); i++) {
                CampeonatoAtleta v = vinculos.get(i);
                if (v.getCampeonatoClube() != null && v.getCampeonatoAtletaId() != null) {
                    transferencias.add(new TransferenciaTimeline(
                            entry.getKey(),
                            v.getCampeonatoAtletaId(),
                            v.getCampeonatoClube().getCampeonatoClubeId()));
                }
            }
        }

        transferencias.sort(Comparator.comparing(TransferenciaTimeline::vinculoId));

        List<CampeonatoPartida> partidas = listarPartidasFinalizadasOrdenadas(campeonato);
        Map<String, Deque<CampeonatoPartida>> pendentes = new HashMap<>();
        List<CampeonatoSuspensao> criadas = new ArrayList<>();
        int transferIdx = 0;

        for (CampeonatoPartida partida : partidas) {
            Long partidaId = partida.getCampeonatoPartidaId() == null ? 0L : partida.getCampeonatoPartidaId();

            while (transferIdx < transferencias.size()
                    && transferencias.get(transferIdx).vinculoId() < partidaId) {
                TransferenciaTimeline t = transferencias.get(transferIdx);
                clubeAtualPorIdentidade.put(t.identidade(), t.clubeId());
                transferIdx++;
            }

            Long mandanteId = partida.getClubeMandante() != null
                    ? partida.getClubeMandante().getCampeonatoClubeId() : null;
            Long visitanteId = partida.getClubeVisitante() != null
                    ? partida.getClubeVisitante().getCampeonatoClubeId() : null;

            for (Map.Entry<String, Deque<CampeonatoPartida>> entry : new ArrayList<>(pendentes.entrySet())) {
                Long clubeAtual = clubeAtualPorIdentidade.get(entry.getKey());
                if (clubeAtual == null) {
                    continue;
                }
                if (!Objects.equals(clubeAtual, mandanteId) && !Objects.equals(clubeAtual, visitanteId)) {
                    continue;
                }
                Deque<CampeonatoPartida> fila = entry.getValue();
                CampeonatoPartida origem = fila.pollFirst();
                if (origem == null) {
                    continue;
                }
                CampeonatoSuspensao suspensao = novaSuspensao(campeonato, entry.getKey(), origem);
                suspensao.setPartidaCumprimento(partida);
                suspensao.setAtiva(false);
                criadas.add(suspensao);
                if (fila.isEmpty()) {
                    pendentes.remove(entry.getKey());
                }
            }

            if (partida.getEventos() != null) {
                for (CampeonatoPartidaEvento evento : partida.getEventos()) {
                    if (evento.getTipo() != TipoEventoPartida.CARTAO_VERMELHO || evento.getAtleta() == null) {
                        continue;
                    }
                    String identidade = CampeonatoAtletaIdentidade.garantir(evento.getAtleta());
                    pendentes.computeIfAbsent(identidade, k -> new ArrayDeque<>()).addLast(partida);
                }
            }
        }

        for (Map.Entry<String, Deque<CampeonatoPartida>> entry : pendentes.entrySet()) {
            for (CampeonatoPartida origem : entry.getValue()) {
                criadas.add(novaSuspensao(campeonato, entry.getKey(), origem));
            }
        }

        if (!criadas.isEmpty()) {
            campeonatoSuspensaoRepository.saveAll(criadas);
        }
    }

    private CampeonatoSuspensao novaSuspensao(Campeonato campeonato, String identidade, CampeonatoPartida origem) {
        CampeonatoSuspensao suspensao = new CampeonatoSuspensao();
        suspensao.setCampeonato(campeonato);
        suspensao.setIdentidade(identidade);
        suspensao.setPartidaOrigem(origem);
        suspensao.setAtiva(true);
        return suspensao;
    }

    private List<CampeonatoPartida> listarPartidasFinalizadasOrdenadas(Campeonato campeonato) {
        List<CampeonatoPartida> partidas = new ArrayList<>();
        if (campeonato.getRodadas() == null) {
            return partidas;
        }
        List<CampeonatoRodada> rodadas = campeonato.getRodadas().stream()
                .sorted(Comparator.comparing(r -> r.getNumeroRodada() == null ? 0 : r.getNumeroRodada()))
                .collect(Collectors.toList());
        for (CampeonatoRodada rodada : rodadas) {
            if (rodada.getPartidas() == null) {
                continue;
            }
            List<CampeonatoPartida> daRodada = rodada.getPartidas().stream()
                    .filter(p -> p.getStatus() == StatusPartida.FINALIZADA)
                    .sorted(Comparator.comparing(p -> p.getOrdem() == null ? 0 : p.getOrdem()))
                    .collect(Collectors.toList());
            for (CampeonatoPartida partida : daRodada) {
                if (partida.getEventos() != null) {
                    partida.getEventos().size();
                }
                partidas.add(partida);
            }
        }
        return partidas;
    }

    private record TransferenciaTimeline(String identidade, Long vinculoId, Long clubeId) {
    }
}
