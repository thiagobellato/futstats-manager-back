package br.com.bellato.gerenciador_fifa.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
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
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
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
 *
 * <p>Conceitos separados:
 * <ul>
 *   <li><b>Histórico</b> — eventos / cartoesAmarelos / cartoesVermelhos (nunca zera)</li>
 *   <li><b>Fila de acúmulo</b> — amarelosAtivos (temporária; não atravessa campeonatos)</li>
 *   <li><b>Suspensões geradas</b> — pendentes/ativas (atravessam campeonatos até cumprimento)</li>
 * </ul>
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
     * Se houver múltiplas, prioriza acúmulo de amarelos (motivo da suspensão atual).
     */
    @Transactional(readOnly = true)
    public Map<String, CampeonatoSuspensao> mapaSuspensoesAtivas(Long campeonatoId) {
        List<CampeonatoSuspensao> ativas = new ArrayList<>(campeonatoSuspensaoRepository
                .findByCampeonatoCampeonatoIdAndAtivaTrue(campeonatoId));
        ativas.sort(Comparator
                .comparing((CampeonatoSuspensao s) -> MotivoSuspensao.fromCodigo(s.getMotivo())
                        != MotivoSuspensao.ACUMULO_AMARELOS)
                .thenComparing(s -> s.getCampeonatoSuspensaoId() == null ? 0L : s.getCampeonatoSuspensaoId()));

        Map<String, CampeonatoSuspensao> mapa = new LinkedHashMap<>();
        for (CampeonatoSuspensao s : ativas) {
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
        }

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
     * Copia suspensões ainda ativas de campeonatos finalizados para o novo campeonato.
     * Amarelos isolados (fila sem suspensão gerada) NÃO são herdados.
     * Suspensões já geradas ATRAVESsam até serem cumpridas.
     */
    @Transactional
    public void herdarSuspensoesPendentes(Campeonato novoCampeonato, List<CampeonatoAtleta> atletasNovo) {
        if (novoCampeonato == null || novoCampeonato.getCampeonatoId() == null
                || atletasNovo == null || atletasNovo.isEmpty()) {
            return;
        }

        Set<String> identidades = new HashSet<>();
        for (CampeonatoAtleta atleta : atletasNovo) {
            identidades.add(CampeonatoAtletaIdentidade.garantir(atleta));
        }

        List<CampeonatoSuspensao> candidatas = campeonatoSuspensaoRepository
                .findAtivasEmCampeonatosComStatus(
                        StatusCampeonato.FINALIZADO, novoCampeonato.getCampeonatoId());

        if (candidatas.isEmpty()) {
            return;
        }

        List<CampeonatoSuspensao> copias = new ArrayList<>();
        List<CampeonatoSuspensao> origemTransferidas = new ArrayList<>();

        for (CampeonatoSuspensao origem : candidatas) {
            if (origem.getIdentidade() == null || !identidades.contains(origem.getIdentidade())) {
                continue;
            }
            CampeonatoSuspensao copia = novaSuspensao(
                    novoCampeonato,
                    origem.getIdentidade(),
                    origem.getPartidaOrigem(),
                    MotivoSuspensao.fromCodigo(origem.getMotivo()),
                    true);
            copias.add(copia);

            origem.setAtiva(false);
            origemTransferidas.add(origem);
        }

        if (!origemTransferidas.isEmpty()) {
            campeonatoSuspensaoRepository.saveAll(origemTransferidas);
        }
        if (!copias.isEmpty()) {
            campeonatoSuspensaoRepository.saveAll(copias);
        }
    }

    /**
     * Recalcula suspensões a partir das partidas finalizadas (idempotente).
     * Ordem de consumo por partida: 1) acúmulo de amarelos, 2) vermelho.
     * Uma partida consome apenas uma punição.
     * Transferências preservam a fila via identidade; o clube atual em cada partida
     * é resolvido pelos eventos dos vínculos (não por comparação de IDs entre tabelas).
     * Suspensões herdadas são preservadas (não apagadas no rebuild).
     */
    @Transactional
    public void recalcularSuspensoes(Campeonato campeonato, List<CampeonatoAtleta> atletas) {
        Long campeonatoId = campeonato.getCampeonatoId();

        // Herdadas persistem entre rebuilds; resetamos cumprimento e reaplicamos de forma idempotente
        List<CampeonatoSuspensao> herdadas = new ArrayList<>(campeonatoSuspensaoRepository
                .findByCampeonatoCampeonatoIdAndHerdadaTrue(campeonatoId));
        for (CampeonatoSuspensao h : herdadas) {
            h.setPartidaCumprimento(null);
            h.setAtiva(true);
        }

        campeonatoSuspensaoRepository.deleteByCampeonatoCampeonatoIdAndHerdadaFalse(campeonatoId);

        Map<String, List<CampeonatoAtleta>> porIdentidade = new HashMap<>();
        for (CampeonatoAtleta atleta : atletas) {
            String identidade = CampeonatoAtletaIdentidade.garantir(atleta);
            porIdentidade.computeIfAbsent(identidade, k -> new ArrayList<>()).add(atleta);
        }

        List<CampeonatoPartida> partidas = listarPartidasFinalizadasOrdenadas(campeonato);
        // Clube por identidade em cada partida: transferência NÃO compara IDs de tabelas distintas.
        // O novo vínculo vale a partir da partida seguinte à última com evento do vínculo anterior.
        Map<String, Long[]> clubePorPartidaIndex = montarClubePorPartidaIndex(porIdentidade, partidas);

        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();
        Map<String, Integer> amarelosAtivos = new HashMap<>();
        List<CampeonatoSuspensao> criadas = new ArrayList<>();

        // Suspensões herdadas entram na fila antes das geradas neste campeonato
        Map<String, Deque<CampeonatoSuspensao>> herdadasPorIdentidade = new HashMap<>();
        herdadas.sort(Comparator
                .comparing((CampeonatoSuspensao s) -> MotivoSuspensao.fromCodigo(s.getMotivo())
                        != MotivoSuspensao.ACUMULO_AMARELOS)
                .thenComparing(s -> s.getCampeonatoSuspensaoId() == null ? 0L : s.getCampeonatoSuspensaoId()));
        for (CampeonatoSuspensao h : herdadas) {
            herdadasPorIdentidade
                    .computeIfAbsent(h.getIdentidade(), k -> new ArrayDeque<>())
                    .addLast(h);
        }

        for (int partidaIndex = 0; partidaIndex < partidas.size(); partidaIndex++) {
            CampeonatoPartida partida = partidas.get(partidaIndex);

            Long mandanteId = partida.getClubeMandante() != null
                    ? partida.getClubeMandante().getCampeonatoClubeId() : null;
            Long visitanteId = partida.getClubeVisitante() != null
                    ? partida.getClubeVisitante().getCampeonatoClubeId() : null;

            Set<String> identidadesNoJogo = new HashSet<>();
            identidadesNoJogo.addAll(herdadasPorIdentidade.keySet());
            identidadesNoJogo.addAll(pendentes.keySet());

            for (String identidade : identidadesNoJogo) {
                Long[] clubes = clubePorPartidaIndex.get(identidade);
                Long clubeAtual = clubes != null && partidaIndex < clubes.length
                        ? clubes[partidaIndex] : null;
                if (clubeAtual == null) {
                    continue;
                }
                if (!Objects.equals(clubeAtual, mandanteId) && !Objects.equals(clubeAtual, visitanteId)) {
                    continue;
                }

                Deque<CampeonatoSuspensao> filaHerdada = herdadasPorIdentidade.get(identidade);
                Deque<PendenteSuspensao> fila = pendentes.get(identidade);

                // Ordem obrigatória: acúmulo de amarelos, depois vermelho (1 punição/partida)
                CampeonatoSuspensao herdadaAmarelo = pollHerdadaPorMotivo(
                        filaHerdada, MotivoSuspensao.ACUMULO_AMARELOS);
                if (herdadaAmarelo != null) {
                    cumprirHerdada(herdadaAmarelo, partida, identidade, amarelosAtivos);
                    limparFilaHerdadaVazia(herdadasPorIdentidade, identidade, filaHerdada);
                    continue;
                }

                PendenteSuspensao pendenteAmarelo = pollPorMotivo(fila, MotivoSuspensao.ACUMULO_AMARELOS);
                if (pendenteAmarelo != null) {
                    criadas.add(cumprirPendente(
                            campeonato, identidade, pendenteAmarelo, partida, amarelosAtivos));
                    limparFilaPendenteVazia(pendentes, identidade, fila);
                    continue;
                }

                CampeonatoSuspensao herdadaVermelho = pollProximaHerdada(filaHerdada);
                if (herdadaVermelho != null) {
                    cumprirHerdada(herdadaVermelho, partida, identidade, amarelosAtivos);
                    limparFilaHerdadaVazia(herdadasPorIdentidade, identidade, filaHerdada);
                    continue;
                }

                PendenteSuspensao origem = pollProximaPunicao(fila);
                if (origem == null) {
                    continue;
                }
                criadas.add(cumprirPendente(campeonato, identidade, origem, partida, amarelosAtivos));
                limparFilaPendenteVazia(pendentes, identidade, fila);
            }

            Map<String, ContagemPartida> porJogador = processarCartoesDaPartida(partida);

            for (Map.Entry<String, ContagemPartida> entry : porJogador.entrySet()) {
                aplicarCartoesNaFila(
                        entry.getKey(),
                        entry.getValue(),
                        partida,
                        amarelosAtivos,
                        pendentes);
            }
        }

        for (Map.Entry<String, Deque<PendenteSuspensao>> entry : pendentes.entrySet()) {
            for (PendenteSuspensao origem : entry.getValue()) {
                criadas.add(novaSuspensao(
                        campeonato, entry.getKey(), origem.partida(), origem.motivo(), false));
            }
        }

        if (!herdadas.isEmpty()) {
            campeonatoSuspensaoRepository.saveAll(herdadas);
        }
        if (!criadas.isEmpty()) {
            campeonatoSuspensaoRepository.saveAll(criadas);
        }
    }

    static CampeonatoSuspensao pollProximaHerdada(Deque<CampeonatoSuspensao> fila) {
        if (fila == null || fila.isEmpty()) {
            return null;
        }
        for (CampeonatoSuspensao s : fila) {
            if (MotivoSuspensao.fromCodigo(s.getMotivo()) == MotivoSuspensao.ACUMULO_AMARELOS) {
                fila.remove(s);
                return s;
            }
        }
        return fila.pollFirst();
    }

    static CampeonatoSuspensao pollHerdadaPorMotivo(Deque<CampeonatoSuspensao> fila, MotivoSuspensao motivo) {
        if (fila == null || fila.isEmpty() || motivo == null) {
            return null;
        }
        for (CampeonatoSuspensao s : fila) {
            if (MotivoSuspensao.fromCodigo(s.getMotivo()) == motivo) {
                fila.remove(s);
                return s;
            }
        }
        return null;
    }

    static PendenteSuspensao pollPorMotivo(Deque<PendenteSuspensao> fila, MotivoSuspensao motivo) {
        if (fila == null || fila.isEmpty() || motivo == null) {
            return null;
        }
        for (PendenteSuspensao p : fila) {
            if (p.motivo() == motivo) {
                fila.remove(p);
                return p;
            }
        }
        return null;
    }

    private static void limparFilaHerdadaVazia(
            Map<String, Deque<CampeonatoSuspensao>> mapa,
            String identidade,
            Deque<CampeonatoSuspensao> fila) {
        if (fila != null && fila.isEmpty()) {
            mapa.remove(identidade);
        }
    }

    private static void limparFilaPendenteVazia(
            Map<String, Deque<PendenteSuspensao>> mapa,
            String identidade,
            Deque<PendenteSuspensao> fila) {
        if (fila != null && fila.isEmpty()) {
            mapa.remove(identidade);
        }
    }

    private void cumprirHerdada(
            CampeonatoSuspensao herdada,
            CampeonatoPartida partida,
            String identidade,
            Map<String, Integer> amarelosAtivos) {
        herdada.setPartidaCumprimento(partida);
        herdada.setAtiva(false);
        if (MotivoSuspensao.fromCodigo(herdada.getMotivo()) == MotivoSuspensao.ACUMULO_AMARELOS) {
            amarelosAtivos.put(identidade, 0);
        }
    }

    private CampeonatoSuspensao cumprirPendente(
            Campeonato campeonato,
            String identidade,
            PendenteSuspensao origem,
            CampeonatoPartida partida,
            Map<String, Integer> amarelosAtivos) {
        CampeonatoSuspensao suspensao = novaSuspensao(
                campeonato, identidade, origem.partida(), origem.motivo(), false);
        suspensao.setPartidaCumprimento(partida);
        suspensao.setAtiva(false);
        if (origem.motivo() == MotivoSuspensao.ACUMULO_AMARELOS) {
            amarelosAtivos.put(identidade, 0);
        }
        return suspensao;
    }

    /**
     * Consome a próxima punição: prioridade acúmulo de amarelos, depois vermelho.
     * Uma chamada = uma punição.
     */
    static PendenteSuspensao pollProximaPunicao(Deque<PendenteSuspensao> fila) {
        if (fila == null || fila.isEmpty()) {
            return null;
        }
        for (PendenteSuspensao p : fila) {
            if (p.motivo() == MotivoSuspensao.ACUMULO_AMARELOS) {
                fila.remove(p);
                return p;
            }
        }
        return fila.pollFirst();
    }

    /**
     * Aplica cartões da partida na fila de acúmulo e gera suspensões.
     * Histórico permanente fica nos eventos — não é alterado aqui.
     */
    static void aplicarCartoesNaFila(
            String identidade,
            ContagemPartida contagem,
            CampeonatoPartida partida,
            Map<String, Integer> amarelosAtivos,
            Map<String, Deque<PendenteSuspensao>> pendentes) {

        boolean doisAmarelosNaPartida = contagem.amarelos >= MAX_AMARELOS_POR_PARTIDA;
        boolean temVermelho = contagem.vermelhos > 0;
        boolean vermelhoDireto = temVermelho && !doisAmarelosNaPartida;

        if (doisAmarelosNaPartida) {
            // Dois amarelos na mesma partida → vermelho automático.
            // Esses amarelos NÃO entram na fila; amarelos anteriores são preservados.
            enfileirar(pendentes, identidade, partida, MotivoSuspensao.SEGUNDO_AMARELO, false);
            return;
        }

        int fila = amarelosAtivos.getOrDefault(identidade, 0) + contagem.amarelos;
        if (fila >= AMARELOS_PARA_ACUMULO) {
            enfileirar(pendentes, identidade, partida, MotivoSuspensao.ACUMULO_AMARELOS, false);
            fila = 0; // convertidos em suspensão gerada
        }
        amarelosAtivos.put(identidade, fila);

        if (vermelhoDireto) {
            // Vermelho direto é independente — nunca zera amarelos acumulados
            enfileirar(pendentes, identidade, partida, MotivoSuspensao.CARTAO_VERMELHO, false);
        }
    }

    private static void enfileirar(
            Map<String, Deque<PendenteSuspensao>> pendentes,
            String identidade,
            CampeonatoPartida partida,
            MotivoSuspensao motivo,
            boolean herdada) {
        pendentes.computeIfAbsent(identidade, k -> new ArrayDeque<>())
                .addLast(new PendenteSuspensao(partida, motivo, herdada));
    }

    private Map<String, ContagemPartida> processarCartoesDaPartida(CampeonatoPartida partida) {
        Map<String, ContagemPartida> mapa = new LinkedHashMap<>();
        if (partida.getEventos() == null) {
            return mapa;
        }
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

    /**
     * Resolve o clube do atleta em cada partida finalizada.
     * Transferências internas criam novo {@link CampeonatoAtleta} com a mesma identidade;
     * o novo clube passa a valer após a última partida em que o vínculo anterior teve eventos.
     * Evita comparar {@code campeonatoAtletaId} com {@code campeonatoPartidaId} (sequences distintas).
     */
    static Map<String, Long[]> montarClubePorPartidaIndex(
            Map<String, List<CampeonatoAtleta>> porIdentidade,
            List<CampeonatoPartida> partidas) {

        Map<String, Long[]> resultado = new HashMap<>();
        if (porIdentidade == null || partidas == null || partidas.isEmpty()) {
            return resultado;
        }

        for (Map.Entry<String, List<CampeonatoAtleta>> entry : porIdentidade.entrySet()) {
            List<CampeonatoAtleta> vinculos = entry.getValue().stream()
                    .sorted(Comparator.comparing(a -> a.getCampeonatoAtletaId() == null ? 0L : a.getCampeonatoAtletaId()))
                    .collect(Collectors.toList());
            if (vinculos.isEmpty()) {
                continue;
            }

            int[] fromIndex = new int[vinculos.size()];
            fromIndex[0] = 0;
            for (int i = 1; i < vinculos.size(); i++) {
                Set<Long> vinculosAnteriores = new HashSet<>();
                for (int k = 0; k < i; k++) {
                    if (vinculos.get(k).getCampeonatoAtletaId() != null) {
                        vinculosAnteriores.add(vinculos.get(k).getCampeonatoAtletaId());
                    }
                }
                int ultimoComEvento = -1;
                for (int j = 0; j < partidas.size(); j++) {
                    if (partidaTemEventoDeAtletas(partidas.get(j), vinculosAnteriores)) {
                        ultimoComEvento = j;
                    }
                }
                fromIndex[i] = ultimoComEvento + 1;
            }

            Long[] clubes = new Long[partidas.size()];
            for (int j = 0; j < partidas.size(); j++) {
                Long clubeId = null;
                for (int i = 0; i < vinculos.size(); i++) {
                    if (fromIndex[i] <= j && vinculos.get(i).getCampeonatoClube() != null) {
                        clubeId = vinculos.get(i).getCampeonatoClube().getCampeonatoClubeId();
                    }
                }
                clubes[j] = clubeId;
            }
            resultado.put(entry.getKey(), clubes);
        }
        return resultado;
    }

    private static boolean partidaTemEventoDeAtletas(CampeonatoPartida partida, Set<Long> atletaIds) {
        if (partida == null || partida.getEventos() == null || atletaIds == null || atletaIds.isEmpty()) {
            return false;
        }
        for (CampeonatoPartidaEvento evento : partida.getEventos()) {
            if (evento.getAtleta() != null
                    && evento.getAtleta().getCampeonatoAtletaId() != null
                    && atletaIds.contains(evento.getAtleta().getCampeonatoAtletaId())) {
                return true;
            }
        }
        return false;
    }

    private CampeonatoSuspensao novaSuspensao(
            Campeonato campeonato,
            String identidade,
            CampeonatoPartida origem,
            MotivoSuspensao motivo,
            boolean herdada) {

        CampeonatoSuspensao suspensao = new CampeonatoSuspensao();
        suspensao.setCampeonato(campeonato);
        suspensao.setIdentidade(identidade);
        suspensao.setPartidaOrigem(origem);
        suspensao.setAtiva(true);
        suspensao.setMotivo(motivo != null ? motivo.getCodigo() : MotivoSuspensao.CARTAO_VERMELHO.getCodigo());
        suspensao.setHerdada(herdada);
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

    static final class ContagemPartida {
        int amarelos;
        int vermelhos;

        ContagemPartida() {
        }

        ContagemPartida(int amarelos, int vermelhos) {
            this.amarelos = amarelos;
            this.vermelhos = vermelhos;
        }
    }

    record PendenteSuspensao(CampeonatoPartida partida, MotivoSuspensao motivo, boolean herdada) {
    }
}
