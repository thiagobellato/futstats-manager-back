package br.com.bellato.gerenciador_fifa.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoEstatisticasDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoFinalizacaoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResumoFinalDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClassificacaoClubeDTO;
import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.model.CampeonatoResultado;
import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.model.HistoricoAtletaCampeonato;
import br.com.bellato.gerenciador_fifa.model.HistoricoClubeCampeonato;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoAtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoResultadoRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaAtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.HistoricoAtletaCampeonatoRepository;
import br.com.bellato.gerenciador_fifa.repository.HistoricoClubeCampeonatoRepository;
import br.com.bellato.gerenciador_fifa.service.finalizacao.RankEvolucaoResultado;
import br.com.bellato.gerenciador_fifa.service.finalizacao.RankEvolutionPolicy;
import br.com.bellato.gerenciador_fifa.service.transferencia.CampeonatoAtletaIdentidade;

/**
 * Finalização oficial do campeonato: sincroniza snapshots → banco global em uma única transação.
 */
@Service
public class CampeonatoFinalizacaoService {

    private static final Logger log = LoggerFactory.getLogger(CampeonatoFinalizacaoService.class);

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private CampeonatoAtletaRepository campeonatoAtletaRepository;

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private EstatisticaAtletaRepository estatisticaAtletaRepository;

    @Autowired
    private CampeonatoResultadoRepository campeonatoResultadoRepository;

    @Autowired
    private HistoricoClubeCampeonatoRepository historicoClubeCampeonatoRepository;

    @Autowired
    private HistoricoAtletaCampeonatoRepository historicoAtletaCampeonatoRepository;

    @Autowired
    private CampeonatoPartidaService campeonatoPartidaService;

    /**
     * Pré-visualização do resumo final (sem persistir). Usa a mesma política de ranks da finalização.
     */
    @Transactional(readOnly = true)
    public CampeonatoResumoFinalDTO obterResumoFinal(Long campeonatoId) {
        Campeonato campeonato = carregarCampeonatoParaFinalizacao(campeonatoId);
        validarAntesDeFinalizar(campeonato);

        CampeonatoEstatisticasDTO estatisticas = campeonatoPartidaService.obterEstatisticas(campeonatoId);
        CampeonatoClube campeao = campeonato.getCampeaoClube();
        CampeonatoClube vice = resolverVice(campeonato, campeao);
        Map<Long, RankEvolucaoResultado> evolucoes = calcularEvolucoesRanks(campeonato, campeao, vice);

        List<ClassificacaoClubeDTO> classificacao = new ArrayList<>(
                estatisticas.getClassificacao() != null ? estatisticas.getClassificacao() : List.of());
        for (ClassificacaoClubeDTO row : classificacao) {
            RankEvolucaoResultado ev = evolucoes.get(row.getCampeonatoClubeId());
            if (ev == null) {
                continue;
            }
            row.setRankInicial(ev.rankInicial() != null ? ev.rankInicial().getSigla() : null);
            row.setRankFinal(ev.rankFinal() != null ? ev.rankFinal().getSigla() : null);
            row.setEvolucaoRank(ev.movimento());
        }

        CampeonatoResumoFinalDTO resumo = new CampeonatoResumoFinalDTO();
        resumo.setCampeonatoId(campeonatoId);
        resumo.setNome(campeonato.getNome());
        resumo.setFormato(campeonato.getQuantidadeClubes());
        resumo.setCampeaoClubeId(campeao.getCampeonatoClubeId());
        resumo.setCampeaoClubeNome(campeao.getNome());
        resumo.setCampeaoCompetidor(campeonato.getCampeaoCompetidor());
        resumo.setCampeaoCompetidorNome(nomeCompetidor(campeonato, campeonato.getCampeaoCompetidor()));

        if (vice != null) {
            resumo.setViceCampeaoClubeId(vice.getCampeonatoClubeId());
            resumo.setViceCampeaoClubeNome(vice.getNome());
            resumo.setViceCompetidor(vice.getCompetidorNumero());
            resumo.setViceCompetidorNome(nomeCompetidor(campeonato, vice.getCompetidorNumero()));
        }

        if (estatisticas.getDashboard() != null) {
            var dash = estatisticas.getDashboard();
            resumo.setArtilheiro(dash.getArtilheiro());
            resumo.setLiderAssistencias(dash.getLiderAssistencias());
            resumo.setMaisCartoesAmarelos(dash.getMaisCartoesAmarelos());
            resumo.setMaisCartoesVermelhos(dash.getMaisCartoesVermelhos());
            resumo.setMelhorAtaque(dash.getMelhorAtaque());
            resumo.setMelhorDefesa(dash.getMelhorDefesa());
            resumo.setMaiorSaldo(dash.getMaiorSaldo());
        }

        resumo.setClassificacao(classificacao);
        return resumo;
    }

    @Transactional
    public CampeonatoFinalizacaoResponseDTO finalizar(Long campeonatoId) {
        Campeonato campeonato = carregarCampeonatoParaFinalizacao(campeonatoId);

        validarAntesDeFinalizar(campeonato);

        // Garante gols contra (e demais eventos) atualizados nos snapshots antes do sync global
        campeonatoPartidaService.recalcularEstatisticasCampeonato(campeonatoId);
        campeonato = carregarCampeonatoParaFinalizacao(campeonatoId);

        List<CampeonatoAtleta> snapshots = campeonatoAtletaRepository.findByCampeonatoCampeonatoId(campeonatoId);
        for (CampeonatoAtleta s : snapshots) {
            CampeonatoAtletaIdentidade.garantir(s);
        }

        Map<Long, Clube> clubesGlobais = carregarClubesGlobais(campeonato.getClubes());
        CampeonatoClube campeaoSnapshot = campeonato.getCampeaoClube();
        CampeonatoClube viceSnapshot = resolverVice(campeonato, campeaoSnapshot);
        Clube campeaoGlobal = clubesGlobais.get(campeaoSnapshot.getClubeOrigemId());
        Clube viceGlobal = viceSnapshot != null ? clubesGlobais.get(viceSnapshot.getClubeOrigemId()) : null;

        Map<String, List<CampeonatoAtleta>> porIdentidade = agruparPorIdentidade(snapshots);
        SyncAtletasResult syncAtletas = sincronizarAtletas(campeonato, porIdentidade, clubesGlobais, campeaoSnapshot);

        int totalRodadas = campeonato.getRodadas() == null ? 1 : Math.max(1, campeonato.getRodadas().size());
        Map<Long, Integer> fasePorClube = calcularFaseAlcancada(campeonato);
        Map<Long, Integer> posicoes = calcularPosicoesFinais(
                campeonato, campeaoSnapshot, viceSnapshot, fasePorClube, totalRodadas);
        Map<Long, RankEvolucaoResultado> evolucoes = calcularEvolucoesRanks(
                campeonato, campeaoSnapshot, posicoes);

        int ranksAlterados = sincronizarClubesERanks(
                campeonato, clubesGlobais, campeaoSnapshot, evolucoes, fasePorClube, posicoes);

        registrarResultado(campeonato, campeaoGlobal, viceGlobal);
        registrarHistoricoAtletas(campeonato, porIdentidade, syncAtletas.atletaPorIdentidade(), campeaoSnapshot);

        LocalDateTime agora = LocalDateTime.now();
        campeonato.setStatus(StatusCampeonato.FINALIZADO);
        campeonato.setDataFinalizacao(agora);
        campeonatoRepository.save(campeonato);

        log.info(
                "Campeonato {} finalizado. Atletas sync={}, criados={}, clubes={}, ranks alterados={}",
                campeonatoId,
                syncAtletas.sincronizados(),
                syncAtletas.criados(),
                clubesGlobais.size(),
                ranksAlterados);

        CampeonatoFinalizacaoResponseDTO response = new CampeonatoFinalizacaoResponseDTO();
        response.setCampeonatoId(campeonatoId);
        response.setNome(campeonato.getNome());
        response.setStatus(StatusCampeonato.FINALIZADO.getCodigo());
        response.setDataFinalizacao(agora);
        response.setCampeaoClubeNome(campeaoSnapshot.getNome());
        response.setViceCampeaoClubeNome(viceSnapshot != null ? viceSnapshot.getNome() : null);
        response.setFormato(campeonato.getQuantidadeClubes());
        response.setAtletasSincronizados(syncAtletas.sincronizados());
        response.setAtletasCriadosGlobalmente(syncAtletas.criados());
        response.setClubesAtualizados(clubesGlobais.size());
        response.setRanksAlterados(ranksAlterados);
        return response;
    }

    private Campeonato carregarCampeonatoParaFinalizacao(Long campeonatoId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new CampeonatoBusinessException("Campeonato não encontrado."));
        if (campeonato.getClubes() != null) {
            campeonato.getClubes().size();
        }
        if (campeonato.getRodadas() != null) {
            campeonato.getRodadas().forEach(r -> {
                if (r.getPartidas() != null) {
                    r.getPartidas().size();
                }
            });
        }
        if (campeonato.getCampeaoClube() != null) {
            campeonato.getCampeaoClube().getNome();
        }
        return campeonato;
    }

    private String nomeCompetidor(Campeonato campeonato, Integer numero) {
        if (Objects.equals(numero, 1)) {
            return campeonato.getCompetidor1Nome();
        }
        if (Objects.equals(numero, 2)) {
            return campeonato.getCompetidor2Nome();
        }
        return null;
    }

    /**
     * Calcula evolução de ranks com a mesma política da finalização (sem persistir).
     */
    Map<Long, RankEvolucaoResultado> calcularEvolucoesRanks(
            Campeonato campeonato,
            CampeonatoClube campeaoSnapshot,
            CampeonatoClube viceSnapshot) {
        int totalRodadas = campeonato.getRodadas() == null ? 1 : Math.max(1, campeonato.getRodadas().size());
        Map<Long, Integer> fasePorClube = calcularFaseAlcancada(campeonato);
        Map<Long, Integer> posicoes = calcularPosicoesFinais(
                campeonato, campeaoSnapshot, viceSnapshot, fasePorClube, totalRodadas);
        return calcularEvolucoesRanks(campeonato, campeaoSnapshot, posicoes);
    }

    private Map<Long, RankEvolucaoResultado> calcularEvolucoesRanks(
            Campeonato campeonato,
            CampeonatoClube campeaoSnapshot,
            Map<Long, Integer> posicoes) {

        Map<Long, RankEvolucaoResultado> mapa = new LinkedHashMap<>();
        if (campeonato.getClubes() == null) {
            return mapa;
        }
        int totalClubes = campeonato.getQuantidadeClubes() != null && campeonato.getQuantidadeClubes() > 0
                ? campeonato.getQuantidadeClubes()
                : campeonato.getClubes().size();

        for (CampeonatoClube snap : campeonato.getClubes()) {
            ClubRank rankBase = snap.getRank() != null ? snap.getRank() : ClubRank.E;
            boolean campeao = Objects.equals(snap.getCampeonatoClubeId(), campeaoSnapshot.getCampeonatoClubeId());
            boolean protegido = Boolean.TRUE.equals(snap.getCampeaoAnterior());
            int posicao = posicoes.getOrDefault(snap.getCampeonatoClubeId(), totalClubes);

            ClubRank rankNovo = RankEvolutionPolicy.calcularNovoRank(
                    snap, posicao, totalClubes, campeao, protegido);
            mapa.put(snap.getCampeonatoClubeId(),
                    new RankEvolucaoResultado(snap.getCampeonatoClubeId(), rankBase, rankNovo));
        }
        return mapa;
    }

    private void validarAntesDeFinalizar(Campeonato campeonato) {
        if (campeonato.getStatus() == StatusCampeonato.FINALIZADO
                || campeonatoResultadoRepository.existsByCampeonatoCampeonatoId(campeonato.getCampeonatoId())) {
            throw new CampeonatoBusinessException(
                    "Este campeonato já foi finalizado. A operação é irreversível e não pode ser executada novamente.");
        }
        if (campeonato.getStatus() != StatusCampeonato.AGUARDANDO_FINALIZACAO) {
            throw new CampeonatoBusinessException(
                    "Só é possível finalizar quando o campeonato estiver aguardando finalização (campeão definido).");
        }
        if (campeonato.getCampeaoClube() == null) {
            throw new CampeonatoBusinessException("Não há campeão definido para este campeonato.");
        }
        if (campeonato.getCampeaoCompetidor() == null) {
            throw new CampeonatoBusinessException("Competidor campeão não definido.");
        }

        List<String> erros = new ArrayList<>();

        if (campeonato.getRodadas() != null) {
            for (CampeonatoRodada rodada : campeonato.getRodadas()) {
                if (rodada.getPartidas() == null) {
                    continue;
                }
                for (CampeonatoPartida partida : rodada.getPartidas()) {
                    if (partida.getStatus() != StatusPartida.FINALIZADA) {
                        erros.add("Existe confronto pendente: rodada "
                                + rodada.getNumeroRodada()
                                + " partida ordem "
                                + partida.getOrdem()
                                + ".");
                    }
                }
            }
        }

        if (campeonato.getClubes() != null) {
            for (CampeonatoClube clube : campeonato.getClubes()) {
                if (clube.getClubeOrigemId() == null) {
                    erros.add("Snapshot de clube inconsistente sem origem global: " + clube.getNome());
                }
            }
        }

        List<CampeonatoAtleta> atletas = campeonatoAtletaRepository
                .findByCampeonatoCampeonatoId(campeonato.getCampeonatoId());
        for (CampeonatoAtleta atleta : atletas) {
            if (atleta.getCampeonatoClube() == null) {
                erros.add("Snapshot de atleta sem clube: " + atleta.getNome());
            }
            if (Boolean.FALSE.equals(atleta.getAtivo()) && atleta.getDataFim() == null) {
                erros.add("Transferência incompleta: vínculo encerrado sem data fim ("
                        + atleta.getNome() + " " + Objects.toString(atleta.getSobrenome(), "") + ").");
            }
        }

        Set<Integer> formatos = Set.of(16, 32, 64);
        if (campeonato.getQuantidadeClubes() == null || !formatos.contains(campeonato.getQuantidadeClubes())) {
            erros.add("Formato do campeonato inválido (esperado 16, 32 ou 64 clubes).");
        }

        if (!erros.isEmpty()) {
            throw new CampeonatoBusinessException(
                    "Não foi possível finalizar o campeonato: " + String.join(" ", erros));
        }
    }

    private Map<Long, Clube> carregarClubesGlobais(List<CampeonatoClube> snapshots) {
        Set<Long> ids = snapshots.stream()
                .map(CampeonatoClube::getClubeOrigemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Clube> mapa = new HashMap<>();
        if (ids.isEmpty()) {
            return mapa;
        }
        for (Clube clube : clubeRepository.findAllById(ids)) {
            mapa.put(clube.getClubeId(), clube);
        }
        for (Long id : ids) {
            if (!mapa.containsKey(id)) {
                throw new CampeonatoBusinessException("Clube global não encontrado (id=" + id + ").");
            }
        }
        return mapa;
    }

    private Map<String, List<CampeonatoAtleta>> agruparPorIdentidade(List<CampeonatoAtleta> snapshots) {
        Map<String, List<CampeonatoAtleta>> mapa = new LinkedHashMap<>();
        for (CampeonatoAtleta s : snapshots) {
            String id = CampeonatoAtletaIdentidade.garantir(s);
            mapa.computeIfAbsent(id, k -> new ArrayList<>()).add(s);
        }
        for (List<CampeonatoAtleta> lista : mapa.values()) {
            lista.sort(Comparator
                    .comparing((CampeonatoAtleta a) -> a.getDataInicio() == null ? LocalDate.MIN : a.getDataInicio())
                    .thenComparing(a -> a.getCampeonatoAtletaId() == null ? 0L : a.getCampeonatoAtletaId()));
        }
        return mapa;
    }

    private SyncAtletasResult sincronizarAtletas(
            Campeonato campeonato,
            Map<String, List<CampeonatoAtleta>> porIdentidade,
            Map<Long, Clube> clubesGlobais,
            CampeonatoClube campeaoSnapshot) {

        int sincronizados = 0;
        int criados = 0;
        Map<String, Atleta> atletaPorIdentidade = new HashMap<>();

        Set<Long> atletaIdsExistentes = porIdentidade.values().stream()
                .flatMap(List::stream)
                .map(CampeonatoAtleta::getAtletaOrigemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Atleta> atletasGlobais = new HashMap<>();
        if (!atletaIdsExistentes.isEmpty()) {
            for (Atleta a : atletaRepository.findAllById(atletaIdsExistentes)) {
                atletasGlobais.put(a.getAtletaId(), a);
            }
        }

        Map<Long, List<EstatisticaAtleta>> statsPorAtleta = new HashMap<>();
        if (!atletaIdsExistentes.isEmpty()) {
            for (EstatisticaAtleta e : estatisticaAtletaRepository
                    .findByAtletaAtletaIdIn(new ArrayList<>(atletaIdsExistentes))) {
                statsPorAtleta
                        .computeIfAbsent(e.getAtleta().getAtletaId(), k -> new ArrayList<>())
                        .add(e);
            }
        }

        List<EstatisticaAtleta> statsParaSalvar = new ArrayList<>();
        List<Atleta> atletasParaSalvar = new ArrayList<>();
        List<CampeonatoAtleta> snapshotsParaSalvar = new ArrayList<>();

        for (Map.Entry<String, List<CampeonatoAtleta>> entry : porIdentidade.entrySet()) {
            String identidade = entry.getKey();
            List<CampeonatoAtleta> vinculos = entry.getValue();
            if (vinculos.isEmpty()) {
                continue;
            }

            Long origemId = vinculos.stream()
                    .map(CampeonatoAtleta::getAtletaOrigemId)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            Atleta atleta;
            if (origemId == null) {
                atleta = criarAtletaGlobal(vinculos, clubesGlobais);
                criados++;
            } else {
                atleta = atletasGlobais.get(origemId);
                if (atleta == null) {
                    throw new CampeonatoBusinessException(
                            "Atleta global não encontrado (id=" + origemId + ") para identidade " + identidade);
                }
            }

            List<EstatisticaAtleta> statsAtleta = statsPorAtleta.computeIfAbsent(
                    atleta.getAtletaId() != null ? atleta.getAtletaId() : -1L,
                    k -> new ArrayList<>());

            aplicarVinculosNoGlobal(atleta, vinculos, clubesGlobais, statsAtleta, statsParaSalvar, origemId == null);

            atletasParaSalvar.add(atleta);
            atletaPorIdentidade.put(identidade, atleta);
            sincronizados++;
        }

        // Persiste atletas novos primeiro (mesmas instâncias referenciadas pelas stats)
        List<Atleta> novosSemId = atletasParaSalvar.stream()
                .filter(a -> a.getAtletaId() == null)
                .distinct()
                .collect(Collectors.toList());
        if (!novosSemId.isEmpty()) {
            atletaRepository.saveAll(novosSemId);
        }

        atletaRepository.saveAll(atletasParaSalvar.stream()
                .filter(a -> a.getAtletaId() != null)
                .distinct()
                .collect(Collectors.toList()));

        for (Map.Entry<String, List<CampeonatoAtleta>> entry : porIdentidade.entrySet()) {
            Atleta ref = atletaPorIdentidade.get(entry.getKey());
            if (ref == null || ref.getAtletaId() == null) {
                continue;
            }
            for (CampeonatoAtleta v : entry.getValue()) {
                if (v.getAtletaOrigemId() == null) {
                    v.setAtletaOrigemId(ref.getAtletaId());
                    v.setIdentidade(CampeonatoAtletaIdentidade.paraAtletaGlobal(ref.getAtletaId()));
                    snapshotsParaSalvar.add(v);
                }
            }
        }

        if (!statsParaSalvar.isEmpty()) {
            estatisticaAtletaRepository.saveAll(statsParaSalvar);
        }
        if (!snapshotsParaSalvar.isEmpty()) {
            campeonatoAtletaRepository.saveAll(snapshotsParaSalvar.stream().distinct().collect(Collectors.toList()));
        }

        return new SyncAtletasResult(sincronizados, criados, atletaPorIdentidade);
    }

    private Atleta criarAtletaGlobal(List<CampeonatoAtleta> vinculos, Map<Long, Clube> clubesGlobais) {
        CampeonatoAtleta ativo = vinculos.stream()
                .filter(CampeonatoAtleta::isAtivo)
                .reduce((a, b) -> b)
                .orElse(vinculos.get(vinculos.size() - 1));

        Clube clubeAtual = clubesGlobais.get(ativo.getCampeonatoClube().getClubeOrigemId());
        Atleta atleta = new Atleta();
        atleta.setNome(ativo.getNome());
        atleta.setSobrenome(ativo.getSobrenome());
        atleta.setDataDeNascimento(ativo.getDataDeNascimento());
        atleta.setNacionalidade(ativo.getNacionalidade());
        atleta.setPosicao(ativo.getPosicao());
        atleta.setClube(clubeAtual);
        return atleta;
    }

    /**
     * Aplica cada vínculo do campeonato ao histórico global, acumulando sem sobrescrever.
     */
    private void aplicarVinculosNoGlobal(
            Atleta atleta,
            List<CampeonatoAtleta> vinculos,
            Map<Long, Clube> clubesGlobais,
            List<EstatisticaAtleta> statsAtleta,
            List<EstatisticaAtleta> statsParaSalvar,
            boolean atletaNovo) {

        EstatisticaAtleta ativaAtual = statsAtleta.stream()
                .filter(e -> e.getDataFim() == null)
                .findFirst()
                .orElse(null);

        for (int i = 0; i < vinculos.size(); i++) {
            CampeonatoAtleta vinculo = vinculos.get(i);
            Clube clubePeriodo = clubesGlobais.get(vinculo.getCampeonatoClube().getClubeOrigemId());
            if (clubePeriodo == null) {
                throw new CampeonatoBusinessException(
                        "Clube global ausente para snapshot " + vinculo.getCampeonatoClube().getNome());
            }

            boolean primeiro = i == 0;
            boolean ultimo = i == vinculos.size() - 1;

            if (primeiro && !atletaNovo && ativaAtual != null
                    && Objects.equals(ativaAtual.getClube().getClubeId(), clubePeriodo.getClubeId())) {
                // Caso 1 / início do Caso 2: acumula no vínculo global vigente
                acumular(ativaAtual, vinculo);
                if (!statsParaSalvar.contains(ativaAtual)) {
                    statsParaSalvar.add(ativaAtual);
                }
                if (!ultimo) {
                    ativaAtual.setDataFim(vinculo.getDataFim() != null ? vinculo.getDataFim() : LocalDate.now());
                    ativaAtual = null;
                }
            } else if (primeiro && !atletaNovo) {
                // Atleta global em outro clube, ou sem ativa no clube do primeiro vínculo
                EstatisticaAtleta alvo = encontrarOuCriarPeriodo(atleta, clubePeriodo, vinculo, statsAtleta, ativaAtual);
                acumular(alvo, vinculo);
                if (!statsParaSalvar.contains(alvo)) {
                    statsParaSalvar.add(alvo);
                }
                if (ativaAtual != null && ativaAtual != alvo && ativaAtual.getDataFim() == null
                        && !Objects.equals(ativaAtual.getClube().getClubeId(), clubePeriodo.getClubeId())) {
                    // não fecha automaticamente o vínculo global ativo de outro clube no primeiro período
                    // se o atleta não transferiu globalmente — só acumula no clube do campeonato
                }
                if (!ultimo && alvo.getDataFim() == null) {
                    alvo.setDataFim(vinculo.getDataFim() != null ? vinculo.getDataFim() : LocalDate.now());
                }
                if (ultimo) {
                    ativaAtual = alvo.getDataFim() == null ? alvo : null;
                } else {
                    ativaAtual = null;
                }
            } else {
                // Transferência no campeonato (ou atleta novo): cria período no clube destino
                if (ativaAtual != null && ativaAtual.getDataFim() == null
                        && !Objects.equals(ativaAtual.getClube().getClubeId(), clubePeriodo.getClubeId())) {
                    ativaAtual.setDataFim(vinculo.getDataInicio() != null
                            ? vinculo.getDataInicio().minusDays(0)
                            : LocalDate.now());
                    if (!statsParaSalvar.contains(ativaAtual)) {
                        statsParaSalvar.add(ativaAtual);
                    }
                }

                EstatisticaAtleta novoPeriodo = new EstatisticaAtleta();
                novoPeriodo.setAtleta(atleta);
                novoPeriodo.setClube(clubePeriodo);
                novoPeriodo.setDataInicio(vinculo.getDataInicio() != null ? vinculo.getDataInicio() : LocalDate.now());
                novoPeriodo.setGols(0);
                novoPeriodo.setAssistencias(0);
                novoPeriodo.setCartaoAmarelo(0);
                novoPeriodo.setCartaoVermelho(0);
                novoPeriodo.setGolsContra(0);
                acumular(novoPeriodo, vinculo);
                if (!ultimo) {
                    novoPeriodo.setDataFim(vinculo.getDataFim() != null ? vinculo.getDataFim() : LocalDate.now());
                }
                statsAtleta.add(novoPeriodo);
                statsParaSalvar.add(novoPeriodo);
                ativaAtual = ultimo ? novoPeriodo : null;
            }
        }

        CampeonatoAtleta vinculoFinal = vinculos.stream()
                .filter(CampeonatoAtleta::isAtivo)
                .reduce((a, b) -> b)
                .orElse(vinculos.get(vinculos.size() - 1));
        Clube clubeFinal = clubesGlobais.get(vinculoFinal.getCampeonatoClube().getClubeOrigemId());
        atleta.setClube(clubeFinal);
    }

    private EstatisticaAtleta encontrarOuCriarPeriodo(
            Atleta atleta,
            Clube clube,
            CampeonatoAtleta vinculo,
            List<EstatisticaAtleta> statsAtleta,
            EstatisticaAtleta ativaAtual) {

        // Prefere estatística ativa do mesmo clube
        for (EstatisticaAtleta e : statsAtleta) {
            if (e.getDataFim() == null
                    && Objects.equals(e.getClube().getClubeId(), clube.getClubeId())) {
                return e;
            }
        }
        // Se a ativa atual é deste clube
        if (ativaAtual != null && ativaAtual.getDataFim() == null
                && Objects.equals(ativaAtual.getClube().getClubeId(), clube.getClubeId())) {
            return ativaAtual;
        }

        EstatisticaAtleta novo = new EstatisticaAtleta();
        novo.setAtleta(atleta);
        novo.setClube(clube);
        novo.setDataInicio(vinculo.getDataInicio() != null ? vinculo.getDataInicio() : LocalDate.now());
        novo.setGols(0);
        novo.setAssistencias(0);
        novo.setCartaoAmarelo(0);
        novo.setCartaoVermelho(0);
        novo.setGolsContra(0);
        statsAtleta.add(novo);
        return novo;
    }

    private void acumular(EstatisticaAtleta alvo, CampeonatoAtleta vinculo) {
        alvo.setGols(nvl(alvo.getGols()) + nvl(vinculo.getGols()));
        alvo.setAssistencias(nvl(alvo.getAssistencias()) + nvl(vinculo.getAssistencias()));
        alvo.setCartaoAmarelo(nvl(alvo.getCartaoAmarelo()) + nvl(vinculo.getCartoesAmarelos()));
        alvo.setCartaoVermelho(nvl(alvo.getCartaoVermelho()) + nvl(vinculo.getCartoesVermelhos()));
        alvo.setGolsContra(nvl(alvo.getGolsContra()) + nvl(vinculo.getGolsContra()));
    }

    private int sincronizarClubesERanks(
            Campeonato campeonato,
            Map<Long, Clube> clubesGlobais,
            CampeonatoClube campeaoSnapshot,
            Map<Long, RankEvolucaoResultado> evolucoes,
            Map<Long, Integer> fasePorClube,
            Map<Long, Integer> posicoes) {

        List<HistoricoClubeCampeonato> historicos = new ArrayList<>();
        List<Clube> clubesParaSalvar = new ArrayList<>();
        int ranksAlterados = 0;

        for (CampeonatoClube snap : campeonato.getClubes()) {
            Clube global = clubesGlobais.get(snap.getClubeOrigemId());
            if (global == null) {
                continue;
            }

            global.setGolsPro(nvl(global.getGolsPro()) + nvl(snap.getGolsPro()));
            global.setGolsContra(nvl(global.getGolsContra()) + nvl(snap.getGolsContra()));
            global.setVitorias(nvl(global.getVitorias()) + nvl(snap.getVitorias()));
            global.setEmpates(nvl(global.getEmpates()) + nvl(snap.getEmpates()));
            global.setDerrotas(nvl(global.getDerrotas()) + nvl(snap.getDerrotas()));

            boolean campeao = Objects.equals(snap.getCampeonatoClubeId(), campeaoSnapshot.getCampeonatoClubeId());
            if (campeao) {
                global.setTitulos(nvl(global.getTitulos()) + 1);
            }

            RankEvolucaoResultado evolucao = evolucoes.get(snap.getCampeonatoClubeId());
            ClubRank rankBase = evolucao != null && evolucao.rankInicial() != null
                    ? evolucao.rankInicial()
                    : (snap.getRank() != null ? snap.getRank() : ClubRank.E);
            ClubRank rankNovo = evolucao != null && evolucao.rankFinal() != null
                    ? evolucao.rankFinal()
                    : rankBase;

            if (rankNovo != rankBase) {
                ranksAlterados++;
            }
            global.setRank(rankNovo);
            clubesParaSalvar.add(global);

            HistoricoClubeCampeonato hist = new HistoricoClubeCampeonato();
            hist.setClube(global);
            hist.setCampeonato(campeonato);
            hist.setPosicaoFinal(posicoes.getOrDefault(snap.getCampeonatoClubeId(), campeonato.getQuantidadeClubes()));
            hist.setRankAnterior(rankBase);
            hist.setRankNovo(rankNovo);
            hist.setTituloConquistado(campeao);
            hist.setEliminado(Boolean.TRUE.equals(snap.getEliminado()));
            hist.setFaseAlcancada(fasePorClube.getOrDefault(snap.getCampeonatoClubeId(), 0));
            hist.setJogos(nvl(snap.getJogos()));
            hist.setVitorias(nvl(snap.getVitorias()));
            hist.setEmpates(nvl(snap.getEmpates()));
            hist.setDerrotas(nvl(snap.getDerrotas()));
            hist.setGolsPro(nvl(snap.getGolsPro()));
            hist.setGolsContra(nvl(snap.getGolsContra()));
            historicos.add(hist);

            log.info("Clube {} rank {} → {} (posição {}, campeão={})",
                    global.getNome(), rankBase, rankNovo,
                    hist.getPosicaoFinal(), campeao);
        }

        clubeRepository.saveAll(clubesParaSalvar);
        historicoClubeCampeonatoRepository.saveAll(historicos);
        return ranksAlterados;
    }

    private void registrarResultado(Campeonato campeonato, Clube campeao, Clube vice) {
        CampeonatoResultado resultado = new CampeonatoResultado();
        resultado.setCampeonato(campeonato);
        resultado.setNomeCampeonato(campeonato.getNome());
        resultado.setCampeaoClube(campeao);
        resultado.setCampeaoCompetidor(campeonato.getCampeaoCompetidor());
        if (Objects.equals(campeonato.getCampeaoCompetidor(), 1)) {
            resultado.setCampeaoCompetidorNome(campeonato.getCompetidor1Nome());
        } else if (Objects.equals(campeonato.getCampeaoCompetidor(), 2)) {
            resultado.setCampeaoCompetidorNome(campeonato.getCompetidor2Nome());
        }
        resultado.setViceCampeaoClube(vice);
        resultado.setDataConquista(LocalDateTime.now());
        resultado.setQuantidadeParticipantes(campeonato.getQuantidadeClubes());
        resultado.setFormato(campeonato.getQuantidadeClubes());
        campeonatoResultadoRepository.save(resultado);
        log.info("Resultado registrado: campeão={}, vice={}, formato={}",
                campeao.getNome(), vice != null ? vice.getNome() : "-", campeonato.getQuantidadeClubes());
    }

    private void registrarHistoricoAtletas(
            Campeonato campeonato,
            Map<String, List<CampeonatoAtleta>> porIdentidade,
            Map<String, Atleta> atletaPorIdentidade,
            CampeonatoClube campeaoSnapshot) {

        Long campeaoClubeId = campeaoSnapshot.getCampeonatoClubeId();
        Set<Long> atletasCampeoes = new HashSet<>();
        List<CampeonatoAtleta> ativosCampeao = campeonatoAtletaRepository
                .findByCampeonatoClubeCampeonatoClubeIdAndAtivoTrue(campeaoClubeId);
        for (CampeonatoAtleta a : ativosCampeao) {
            if (a.getAtletaOrigemId() != null) {
                atletasCampeoes.add(a.getAtletaOrigemId());
            }
        }

        List<HistoricoAtletaCampeonato> historicos = new ArrayList<>();
        for (Map.Entry<String, List<CampeonatoAtleta>> entry : porIdentidade.entrySet()) {
            Atleta atleta = atletaPorIdentidade.get(entry.getKey());
            if (atleta == null || atleta.getAtletaId() == null) {
                continue;
            }
            List<CampeonatoAtleta> vinculos = entry.getValue();
            int gols = 0;
            int assistencias = 0;
            int amarelos = 0;
            int vermelhos = 0;
            int golsContra = 0;
            List<String> clubes = new ArrayList<>();
            for (CampeonatoAtleta v : vinculos) {
                gols += nvl(v.getGols());
                assistencias += nvl(v.getAssistencias());
                amarelos += nvl(v.getCartoesAmarelos());
                vermelhos += nvl(v.getCartoesVermelhos());
                golsContra += nvl(v.getGolsContra());
                String nomeClube = v.getCampeonatoClube() != null ? v.getCampeonatoClube().getNome() : null;
                if (nomeClube != null && !clubes.contains(nomeClube)) {
                    clubes.add(nomeClube);
                }
            }

            HistoricoAtletaCampeonato hist = new HistoricoAtletaCampeonato();
            hist.setAtleta(atleta);
            hist.setCampeonato(campeonato);
            hist.setGols(gols);
            hist.setAssistencias(assistencias);
            hist.setCartoesAmarelos(amarelos);
            hist.setCartoesVermelhos(vermelhos);
            hist.setGolsContra(golsContra);
            hist.setTransferencias(Math.max(0, vinculos.size() - 1));
            hist.setClubesDefendidos(String.join(", ", clubes));
            hist.setTituloConquistado(atletasCampeoes.contains(atleta.getAtletaId())
                    || vinculos.stream().anyMatch(v -> v.isAtivo()
                            && v.getCampeonatoClube() != null
                            && Objects.equals(v.getCampeonatoClube().getCampeonatoClubeId(), campeaoClubeId)));
            historicos.add(hist);
        }
        historicoAtletaCampeonatoRepository.saveAll(historicos);
    }

    private CampeonatoClube resolverVice(Campeonato campeonato, CampeonatoClube campeao) {
        CampeonatoClube melhorOponente = null;
        int melhorRodada = -1;

        if (campeonato.getRodadas() == null) {
            return null;
        }
        for (CampeonatoRodada rodada : campeonato.getRodadas()) {
            if (rodada.getPartidas() == null) {
                continue;
            }
            int num = rodada.getNumeroRodada() == null ? 0 : rodada.getNumeroRodada();
            for (CampeonatoPartida partida : rodada.getPartidas()) {
                if (partida.getStatus() != StatusPartida.FINALIZADA
                        || partida.getClubeVencedor() == null) {
                    continue;
                }
                boolean envolveCampeao =
                        (partida.getClubeMandante() != null
                                && Objects.equals(partida.getClubeMandante().getCampeonatoClubeId(),
                                        campeao.getCampeonatoClubeId()))
                        || (partida.getClubeVisitante() != null
                                && Objects.equals(partida.getClubeVisitante().getCampeonatoClubeId(),
                                        campeao.getCampeonatoClubeId()));
                if (!envolveCampeao) {
                    continue;
                }
                if (!Objects.equals(partida.getClubeVencedor().getCampeonatoClubeId(), campeao.getCampeonatoClubeId())) {
                    continue;
                }
                if (partida.getClubeMandante() == null || partida.getClubeVisitante() == null) {
                    continue;
                }
                CampeonatoClube oponente = Objects.equals(
                        partida.getClubeMandante().getCampeonatoClubeId(), campeao.getCampeonatoClubeId())
                        ? partida.getClubeVisitante()
                        : partida.getClubeMandante();
                if (num >= melhorRodada) {
                    melhorRodada = num;
                    melhorOponente = oponente;
                }
            }
        }
        return melhorOponente;
    }

    private Map<Long, Integer> calcularFaseAlcancada(Campeonato campeonato) {
        Map<Long, Integer> fase = new HashMap<>();
        if (campeonato.getClubes() != null) {
            for (CampeonatoClube c : campeonato.getClubes()) {
                fase.put(c.getCampeonatoClubeId(), 0);
            }
        }
        if (campeonato.getRodadas() == null) {
            return fase;
        }
        for (CampeonatoRodada rodada : campeonato.getRodadas()) {
            int num = rodada.getNumeroRodada() == null ? 0 : rodada.getNumeroRodada();
            if (rodada.getPartidas() == null) {
                continue;
            }
            for (CampeonatoPartida p : rodada.getPartidas()) {
                if (p.getClubeMandante() != null) {
                    fase.merge(p.getClubeMandante().getCampeonatoClubeId(), num, Math::max);
                }
                if (p.getClubeVisitante() != null) {
                    fase.merge(p.getClubeVisitante().getCampeonatoClubeId(), num, Math::max);
                }
            }
        }
        return fase;
    }

    /**
     * Posições da classificação estatística (pontos, saldo, GP…),
     * sem privilegiar campeão/vice do mata-mata.
     */
    private Map<Long, Integer> calcularPosicoesFinais(
            Campeonato campeonato,
            CampeonatoClube campeao,
            CampeonatoClube vice,
            Map<Long, Integer> fasePorClube,
            int totalRodadas) {

        Map<Long, Integer> posicoes = new HashMap<>();
        if (campeonato.getClubes() == null || campeonato.getClubes().isEmpty()) {
            return posicoes;
        }

        List<CampeonatoClube> ordenados = campeonato.getClubes().stream()
                .sorted(Comparator
                        .comparing((CampeonatoClube c) -> nvl(c.getPontos()), Comparator.reverseOrder())
                        .thenComparing(CampeonatoClube::getSaldoGols, Comparator.reverseOrder())
                        .thenComparing(c -> nvl(c.getGolsPro()), Comparator.reverseOrder())
                        .thenComparing(c -> nvl(c.getVitorias()), Comparator.reverseOrder())
                        .thenComparing(c -> nvl(c.getDerrotas()))
                        .thenComparing(CampeonatoClube::getNome, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());

        int pos = 1;
        for (CampeonatoClube c : ordenados) {
            posicoes.put(c.getCampeonatoClubeId(), pos++);
        }
        return posicoes;
    }

    private static int nvl(Integer v) {
        return v == null ? 0 : v;
    }

    private record SyncAtletasResult(
            int sincronizados,
            int criados,
            Map<String, Atleta> atletaPorIdentidade) {
    }
}
