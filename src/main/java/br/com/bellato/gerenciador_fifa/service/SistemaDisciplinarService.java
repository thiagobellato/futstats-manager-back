package br.com.bellato.gerenciador_fifa.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.campeonato.PartidaEventoRequestDTO;
import br.com.bellato.gerenciador_fifa.enums.MotivoSuspensao;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartidaEvento;
import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;
import br.com.bellato.gerenciador_fifa.model.CampeonatoSuspensao;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoSuspensaoRepository;
import br.com.bellato.gerenciador_fifa.service.transferencia.CampeonatoAtletaIdentidade;

/**
 * Serviço único responsável por toda a disciplina do campeonato:
 * cartões, segundo amarelo → vermelho automático, suspensões, liberação e bloqueios.
 * Usa a identidade do atleta (sobrevive a transferências internas).
 */
@Service
public class SistemaDisciplinarService {

    public static final int MAX_AMARELOS_POR_PARTIDA = 2;
    public static final int MAX_VERMELHOS_POR_PARTIDA = 1;
    public static final int AMARELOS_PARA_ACUMULO = 2;

    /** @deprecated Preferir MotivoSuspensao; mantido para compatibilidade. */
    public static final String MOTIVO_EXPULSAO = MotivoSuspensao.CARTAO_VERMELHO.getDescricao();

    @Autowired
    private CampeonatoSuspensaoRepository campeonatoSuspensaoRepository;

    @Transactional(readOnly = true)
    public Set<String> identidadesSuspensasAtivas(Long campeonatoId) {
        return mapaSuspensoesAtivas(campeonatoId).keySet();
    }

    /**
     * Suspensões ativas indexadas pela identidade do atleta.
     */
    @Transactional(readOnly = true)
    public Map<String, CampeonatoSuspensao> mapaSuspensoesAtivas(Long campeonatoId) {
        Map<String, CampeonatoSuspensao> mapa = new LinkedHashMap<>();
        for (CampeonatoSuspensao s : campeonatoSuspensaoRepository
                .findByCampeonatoCampeonatoIdAndAtivaTrue(campeonatoId)) {
            if (s.getIdentidade() != null) {
                mapa.putIfAbsent(s.getIdentidade(), s);
            }
        }
        return mapa;
    }

    @Transactional(readOnly = true)
    public ResumoDisciplinar resumoSuspensoesAtivas(Long campeonatoId) {
        List<CampeonatoSuspensao> ativas = campeonatoSuspensaoRepository
                .findByCampeonatoCampeonatoIdAndAtivaTrue(campeonatoId);
        int porVermelho = 0;
        int porAmarelo = 0;
        for (CampeonatoSuspensao s : ativas) {
            MotivoSuspensao motivo = MotivoSuspensao.fromCodigo(s.getMotivo());
            if (motivo == MotivoSuspensao.ACUMULO_AMARELOS) {
                porAmarelo++;
            } else {
                porVermelho++;
            }
        }
        return new ResumoDisciplinar(ativas.size(), porVermelho, porAmarelo);
    }

    public static MotivoSuspensao resolverMotivo(CampeonatoSuspensao suspensao) {
        if (suspensao == null) {
            return MotivoSuspensao.CARTAO_VERMELHO;
        }
        return MotivoSuspensao.fromCodigo(suspensao.getMotivo());
    }

    public static String descricaoMotivo(CampeonatoSuspensao suspensao) {
        return resolverMotivo(suspensao).getDescricao();
    }

    public static String tipoUiMotivo(CampeonatoSuspensao suspensao) {
        return resolverMotivo(suspensao).getTipoUi();
    }

    /**
     * Garante estado disciplinar consistente da partida (pós-jogo, ordem irrelevante).
     * <ul>
     *   <li>máx. 2 amarelos e 1 vermelho por atleta;</li>
     *   <li>2 amarelos ⇒ exatamente 1 vermelho (insere se faltar; remove extras);</li>
     *   <li>não duplica vermelho quando o cliente já enviou o automático.</li>
     * </ul>
     */
    public List<PartidaEventoRequestDTO> recalcularEstadoDisciplinarDaPartida(
            List<PartidaEventoRequestDTO> eventos) {

        if (eventos == null || eventos.isEmpty()) {
            return eventos == null ? List.of() : eventos;
        }

        Map<Long, Integer> amarelosFinais = new HashMap<>();
        Map<Long, Integer> vermelhosOriginais = new HashMap<>();
        Map<Long, PartidaEventoRequestDTO> referenciaAtleta = new HashMap<>();

        for (PartidaEventoRequestDTO evento : eventos) {
            if (evento == null || evento.getCampeonatoAtletaId() == null || evento.getTipo() == null) {
                continue;
            }
            Long atletaId = evento.getCampeonatoAtletaId();
            referenciaAtleta.putIfAbsent(atletaId, evento);
            if (evento.getTipo() == TipoEventoPartida.CARTAO_AMARELO) {
                amarelosFinais.merge(atletaId, 1, Integer::sum);
            } else if (evento.getTipo() == TipoEventoPartida.CARTAO_VERMELHO) {
                vermelhosOriginais.merge(atletaId, 1, Integer::sum);
            }
        }

        for (Map.Entry<Long, Integer> entry : amarelosFinais.entrySet()) {
            entry.setValue(Math.min(entry.getValue(), MAX_AMARELOS_POR_PARTIDA));
        }

        Map<Long, Integer> amarelosEmitidos = new HashMap<>();
        Map<Long, Integer> vermelhosEmitidos = new HashMap<>();
        List<PartidaEventoRequestDTO> resultado = new ArrayList<>();

        for (PartidaEventoRequestDTO evento : eventos) {
            if (evento == null || evento.getTipo() == null) {
                continue;
            }
            if (evento.getCampeonatoAtletaId() == null
                    || (evento.getTipo() != TipoEventoPartida.CARTAO_AMARELO
                    && evento.getTipo() != TipoEventoPartida.CARTAO_VERMELHO)) {
                resultado.add(evento);
                continue;
            }

            Long atletaId = evento.getCampeonatoAtletaId();
            if (evento.getTipo() == TipoEventoPartida.CARTAO_AMARELO) {
                int emitidos = amarelosEmitidos.getOrDefault(atletaId, 0);
                int limite = amarelosFinais.getOrDefault(atletaId, 0);
                if (emitidos >= limite) {
                    continue;
                }
                amarelosEmitidos.put(atletaId, emitidos + 1);
                resultado.add(evento);
            } else {
                if (vermelhosEmitidos.getOrDefault(atletaId, 0) >= MAX_VERMELHOS_POR_PARTIDA) {
                    continue;
                }
                int amarelos = amarelosFinais.getOrDefault(atletaId, 0);
                boolean obrigatorioPorSegundoAmarelo = amarelos >= MAX_AMARELOS_POR_PARTIDA;
                boolean manterDireto = !obrigatorioPorSegundoAmarelo
                        && vermelhosOriginais.getOrDefault(atletaId, 0) > 0;
                if (!obrigatorioPorSegundoAmarelo && !manterDireto) {
                    continue;
                }
                vermelhosEmitidos.merge(atletaId, 1, Integer::sum);
                resultado.add(evento);
            }
        }

        for (Map.Entry<Long, Integer> entry : amarelosFinais.entrySet()) {
            Long atletaId = entry.getKey();
            if (entry.getValue() < MAX_AMARELOS_POR_PARTIDA) {
                continue;
            }
            if (vermelhosEmitidos.getOrDefault(atletaId, 0) >= MAX_VERMELHOS_POR_PARTIDA) {
                continue;
            }
            PartidaEventoRequestDTO ref = referenciaAtleta.get(atletaId);
            if (ref == null) {
                continue;
            }
            PartidaEventoRequestDTO vermelho = new PartidaEventoRequestDTO();
            vermelho.setTipo(TipoEventoPartida.CARTAO_VERMELHO);
            vermelho.setCampeonatoAtletaId(atletaId);
            resultado.add(vermelho);
            vermelhosEmitidos.merge(atletaId, 1, Integer::sum);
        }

        return resultado;
    }

    /** @deprecated Preferir {@link #recalcularEstadoDisciplinarDaPartida(List)}. */
    public List<PartidaEventoRequestDTO> normalizarSegundoAmarelo(List<PartidaEventoRequestDTO> eventos) {
        return recalcularEstadoDisciplinarDaPartida(eventos);
    }

    /**
     * Valida apenas inconsistências disciplinares da partida (limites de cartões).
     * Não bloqueia gols/assistências/gols contra após expulsão — lançamento é pós-jogo.
     */
    public void validarEventosDisciplinares(
            List<PartidaEventoRequestDTO> eventos,
            List<CampeonatoAtleta> atletasResolvidos) {

        if (eventos == null || eventos.isEmpty()) {
            return;
        }
        if (atletasResolvidos == null || eventos.size() != atletasResolvidos.size()) {
            throw new CampeonatoBusinessException("Falha ao resolver atletas dos eventos da partida.");
        }

        Map<Long, Integer> amarelos = new HashMap<>();
        Map<Long, Integer> vermelhos = new HashMap<>();
        Map<Long, String> nomes = new HashMap<>();

        for (int i = 0; i < eventos.size(); i++) {
            PartidaEventoRequestDTO evento = eventos.get(i);
            CampeonatoAtleta atleta = atletasResolvidos.get(i);
            if (evento == null || evento.getTipo() == null || atleta == null) {
                continue;
            }

            Long atletaId = atleta.getCampeonatoAtletaId();
            nomes.putIfAbsent(atletaId, nomeAtleta(atleta));

            TipoEventoPartida tipo = evento.getTipo();
            if (tipo == TipoEventoPartida.CARTAO_AMARELO) {
                int qtd = amarelos.merge(atletaId, 1, Integer::sum);
                if (qtd > MAX_AMARELOS_POR_PARTIDA) {
                    throw new CampeonatoBusinessException(
                            "O atleta " + nomes.get(atletaId) + " já possui o máximo de "
                                    + MAX_AMARELOS_POR_PARTIDA + " cartões amarelos nesta partida.");
                }
            } else if (tipo == TipoEventoPartida.CARTAO_VERMELHO) {
                int qtd = vermelhos.merge(atletaId, 1, Integer::sum);
                if (qtd > MAX_VERMELHOS_POR_PARTIDA) {
                    throw new CampeonatoBusinessException(
                            "O atleta " + nomes.get(atletaId) + " já possui cartão vermelho nesta partida.");
                }
            }
            // GOL, GOL_CONTRA e ASSISTENCIA não são bloqueados por expulsão nesta partida
        }

        // Garante que todo segundo amarelo tenha vermelho correspondente
        for (Map.Entry<Long, Integer> entry : amarelos.entrySet()) {
            if (entry.getValue() >= MAX_AMARELOS_POR_PARTIDA
                    && vermelhos.getOrDefault(entry.getKey(), 0) < 1) {
                throw new CampeonatoBusinessException(
                        "O segundo amarelo de "
                                + nomes.getOrDefault(entry.getKey(), "um atleta")
                                + " exige cartão vermelho automático.");
            }
        }
    }

    /**
     * Recalcula suspensões a partir das partidas finalizadas (idempotente).
     * Ordem: rodada → ordem da partida. Transferências aplicadas por id do vínculo
     * entre partidas, preservando a regra de “próxima partida do clube atual”.
     */
    @Transactional
    public void recalcularSuspensoes(Campeonato campeonato, List<CampeonatoAtleta> atletas) {
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
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();
        Map<String, Integer> amarelosAtivos = new HashMap<>();
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

            for (Map.Entry<String, Deque<PendenteSuspensao>> entry : new ArrayList<>(pendentes.entrySet())) {
                Long clubeAtual = clubeAtualPorIdentidade.get(entry.getKey());
                if (clubeAtual == null) {
                    continue;
                }
                if (!Objects.equals(clubeAtual, mandanteId) && !Objects.equals(clubeAtual, visitanteId)) {
                    continue;
                }
                Deque<PendenteSuspensao> fila = entry.getValue();
                PendenteSuspensao origem = fila.pollFirst();
                if (origem == null) {
                    continue;
                }
                CampeonatoSuspensao suspensao = novaSuspensao(
                        campeonato, entry.getKey(), origem.partida(), origem.motivo());
                suspensao.setPartidaCumprimento(partida);
                suspensao.setAtiva(false);
                criadas.add(suspensao);
                amarelosAtivos.put(entry.getKey(), 0);
                if (fila.isEmpty()) {
                    pendentes.remove(entry.getKey());
                }
            }

            Map<String, ContagemPartida> porJogador = processarCartoesDaPartida(partida);

            for (Map.Entry<String, ContagemPartida> entry : porJogador.entrySet()) {
                String identidade = entry.getKey();
                ContagemPartida contagem = entry.getValue();
                int ativos = amarelosAtivos.getOrDefault(identidade, 0) + contagem.amarelos;

                if (contagem.vermelhos > 0) {
                    MotivoSuspensao motivo = contagem.amarelos >= MAX_AMARELOS_POR_PARTIDA
                            ? MotivoSuspensao.SEGUNDO_AMARELO
                            : MotivoSuspensao.CARTAO_VERMELHO;
                    pendentes.computeIfAbsent(identidade, k -> new ArrayDeque<>())
                            .addLast(new PendenteSuspensao(partida, motivo));
                    // Vermelho encerra o ciclo de amarelos ativos (histórico permanece nos eventos)
                    amarelosAtivos.put(identidade, 0);
                } else {
                    amarelosAtivos.put(identidade, ativos);
                    if (ativos >= AMARELOS_PARA_ACUMULO) {
                        pendentes.computeIfAbsent(identidade, k -> new ArrayDeque<>())
                                .addLast(new PendenteSuspensao(partida, MotivoSuspensao.ACUMULO_AMARELOS));
                        // Contador zera no cumprimento; evita nova suspensão enquanto pendente
                        amarelosAtivos.put(identidade, 0);
                    }
                }
            }
        }

        for (Map.Entry<String, Deque<PendenteSuspensao>> entry : pendentes.entrySet()) {
            for (PendenteSuspensao origem : entry.getValue()) {
                criadas.add(novaSuspensao(campeonato, entry.getKey(), origem.partida(), origem.motivo()));
            }
        }

        if (!criadas.isEmpty()) {
            campeonatoSuspensaoRepository.saveAll(criadas);
        }
    }

    private Map<String, ContagemPartida> processarCartoesDaPartida(CampeonatoPartida partida) {
        Map<String, ContagemPartida> mapa = new LinkedHashMap<>();
        if (partida.getEventos() == null) {
            return mapa;
        }
        // Totais por atleta — ordem dos eventos não importa (lançamento pós-jogo)
        for (CampeonatoPartidaEvento evento : partida.getEventos()) {
            if (evento.getAtleta() == null || evento.getTipo() == null) {
                continue;
            }
            String identidade = CampeonatoAtletaIdentidade.garantir(evento.getAtleta());
            ContagemPartida c = mapa.computeIfAbsent(identidade, k -> new ContagemPartida());

            if (evento.getTipo() == TipoEventoPartida.CARTAO_AMARELO) {
                c.amarelos++;
            } else if (evento.getTipo() == TipoEventoPartida.CARTAO_VERMELHO) {
                c.vermelhos++;
            }
        }
        return mapa;
    }

    private CampeonatoSuspensao novaSuspensao(
            Campeonato campeonato,
            String identidade,
            CampeonatoPartida origem,
            MotivoSuspensao motivo) {

        CampeonatoSuspensao suspensao = new CampeonatoSuspensao();
        suspensao.setCampeonato(campeonato);
        suspensao.setIdentidade(identidade);
        suspensao.setPartidaOrigem(origem);
        suspensao.setAtiva(true);
        suspensao.setMotivo(motivo != null ? motivo.getCodigo() : MotivoSuspensao.CARTAO_VERMELHO.getCodigo());
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

    private static String nomeAtleta(CampeonatoAtleta atleta) {
        if (atleta.getSobrenome() == null || atleta.getSobrenome().isBlank()) {
            return atleta.getNome();
        }
        return atleta.getNome() + " " + atleta.getSobrenome();
    }

    public record ResumoDisciplinar(int total, int porVermelho, int porAmarelo) {
    }

    private static final class ContagemPartida {
        private int amarelos;
        private int vermelhos;
    }

    private record PendenteSuspensao(CampeonatoPartida partida, MotivoSuspensao motivo) {
    }

    private record TransferenciaTimeline(String identidade, Long vinculoId, Long clubeId) {
    }
}
