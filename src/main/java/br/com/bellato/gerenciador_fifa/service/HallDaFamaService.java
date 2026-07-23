package br.com.bellato.gerenciador_fifa.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoEstatisticasDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClassificacaoClubeDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RankingAtletaCampeonatoDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallBuscaResultadoDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallBuscaResultadoDTO.HallBuscaItemDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallCampeonatoDetalheDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallCampeonatoResumoDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallClubePerfilDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallCompetidorPerfilDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallCompetidorPerfilDTO.HallTimelineCompetidorItemDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallGoleadaDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallJogadorPerfilDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallJogadorPerfilDTO.HallTimelineJogadorItemDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallRankingPageDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallRecordeItemDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallRecordesDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallSequenciaDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallTimelineClubeItemDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallTimelineItemDTO;
import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.HallRankingChave;
import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;
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
import br.com.bellato.gerenciador_fifa.repository.CampeonatoResultadoRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaAtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.HistoricoAtletaCampeonatoRepository;
import br.com.bellato.gerenciador_fifa.repository.HistoricoClubeCampeonatoRepository;
import br.com.bellato.gerenciador_fifa.service.transferencia.CampeonatoAtletaIdentidade;

/**
 * Módulo somente leitura do Hall da Fama / Histórico.
 * Consome snapshots e históricos já persistidos na finalização — não altera regras de jogo.
 */
@Service
@Transactional(readOnly = true)
public class HallDaFamaService {

    private static final int TOP = 10;
    private static final int PAGE_SIZE_DEFAULT = 10;

    @Autowired
    private CampeonatoResultadoRepository campeonatoResultadoRepository;

    @Autowired
    private HistoricoClubeCampeonatoRepository historicoClubeRepository;

    @Autowired
    private HistoricoAtletaCampeonatoRepository historicoAtletaRepository;

    @Autowired
    private CampeonatoPartidaService campeonatoPartidaService;

    @Autowired
    private CampeonatoService campeonatoService;

    @Autowired
    private CampeonatoAtletaRepository campeonatoAtletaRepository;

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private EstatisticaClubeRepository estatisticaClubeRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private EstatisticaAtletaRepository estatisticaAtletaRepository;

    public List<HallCampeonatoResumoDTO> listarCampeonatos(
            String busca,
            Integer ano,
            Integer quantidadeClubes,
            String competidor,
            String clube,
            String campeao,
            String ranking) {

        List<CampeonatoResultado> resultados = campeonatoResultadoRepository.findAllComDetalhesOrderByDataDesc();
        List<HallCampeonatoResumoDTO> lista = new ArrayList<>();

        for (CampeonatoResultado resultado : resultados) {
            Campeonato campeonato = resultado.getCampeonato();
            if (campeonato == null || campeonato.getStatus() != StatusCampeonato.FINALIZADO) {
                continue;
            }
            CampeonatoEstatisticasDTO stats = campeonatoPartidaService.obterEstatisticas(campeonato.getCampeonatoId());
            HallCampeonatoResumoDTO dto = toResumo(resultado, campeonato, stats);
            if (passarFiltros(dto, campeonato, resultado, busca, ano, quantidadeClubes, competidor, clube, campeao,
                    ranking)) {
                lista.add(dto);
            }
        }
        return lista;
    }

    public HallCampeonatoDetalheDTO obterCampeonato(Long campeonatoId) {
        CampeonatoResultado resultado = campeonatoResultadoRepository.findDetalhadoByCampeonatoId(campeonatoId)
                .orElseThrow(() -> new CampeonatoBusinessException("Campeonato histórico não encontrado."));

        Campeonato campeonato = resultado.getCampeonato();
        if (campeonato.getStatus() != StatusCampeonato.FINALIZADO) {
            throw new CampeonatoBusinessException("O Hall da Fama só exibe campeonatos finalizados.");
        }

        CampeonatoEstatisticasDTO estatisticas = campeonatoPartidaService.obterEstatisticas(campeonatoId);
        enriquecerRanksClassificacao(campeonatoId, estatisticas);

        CampeonatoResponseCompletoDTO completo = campeonatoService.obterCompletoPorId(campeonatoId);
        List<CampeonatoAtleta> atletas = campeonatoAtletaRepository.findByCampeonatoCampeonatoId(campeonatoId);

        HallCampeonatoDetalheDTO detalhe = new HallCampeonatoDetalheDTO();
        detalhe.setResumo(toResumo(resultado, campeonato, estatisticas));
        detalhe.setCampeonato(completo);
        detalhe.setEstatisticas(estatisticas);
        detalhe.setGolsContra(montarRankingGolsContra(atletas));
        detalhe.setGoleirosMenosVazados(montarGoleirosMenosVazados(atletas, estatisticas));
        preencherExtremosClubes(detalhe, estatisticas);
        detalhe.setMaiorGoleada(calcularMaiorGoleada(campeonato));
        preencherSequencias(detalhe, campeonato);
        return detalhe;
    }

    public List<HallTimelineItemDTO> obterTimeline() {
        List<CampeonatoResultado> resultados = campeonatoResultadoRepository.findAllComDetalhesOrderByDataDesc();
        List<HallTimelineItemDTO> timeline = new ArrayList<>();

        for (CampeonatoResultado resultado : resultados) {
            Campeonato campeonato = resultado.getCampeonato();
            if (campeonato == null || campeonato.getStatus() != StatusCampeonato.FINALIZADO) {
                continue;
            }
            CampeonatoEstatisticasDTO stats = campeonatoPartidaService.obterEstatisticas(campeonato.getCampeonatoId());

            HallTimelineItemDTO item = new HallTimelineItemDTO();
            item.setCampeonatoId(campeonato.getCampeonatoId());
            item.setNome(resultado.getNomeCampeonato());
            item.setDataFinalizacao(resultado.getDataConquista());
            item.setAno(anoDe(resultado.getDataConquista()));
            item.setQuantidadeClubes(resultado.getQuantidadeParticipantes());
            if (resultado.getCampeaoClube() != null) {
                item.setCampeaoClubeId(resultado.getCampeaoClube().getClubeId());
                item.setCampeaoClubeNome(resultado.getCampeaoClube().getNome());
            }
            item.setCampeaoCompetidorNome(resultado.getCampeaoCompetidorNome());
            if (stats.getDashboard() != null) {
                item.setArtilheiro(stats.getDashboard().getArtilheiro());
                item.setLiderAssistencias(stats.getDashboard().getLiderAssistencias());
            }
            timeline.add(item);
        }
        return timeline;
    }

    /**
     * Recordes de competidores (poucos registros — sem paginação).
     * Clubes e jogadores usam {@link #obterRankingPaginado}.
     */
    public HallRecordesDTO obterRecordes() {
        List<CampeonatoResultado> resultados = campeonatoResultadoRepository.findAllComDetalhesOrderByDataDesc();
        List<HistoricoClubeCampeonato> historicosClube = historicoClubeRepository.findAllComDetalhes();

        HallRecordesDTO dto = new HallRecordesDTO();
        Map<String, CompetidorAgg> competidores = agregarCompetidores(resultados, historicosClube);
        dto.setCompetidoresMaisTitulos(todosCompetidor(competidores, c -> c.titulos, "títulos"));
        dto.setCompetidoresMaisFinais(todosCompetidor(competidores, c -> c.finais, "finais"));
        dto.setCompetidoresMaisVices(todosCompetidor(competidores, c -> c.vices, "vices"));
        dto.setCompetidoresMelhorAproveitamento(todosCompetidorAproveitamento(competidores));
        dto.setCompetidoresMaisVitorias(todosCompetidor(competidores, c -> c.vitorias, "vitórias"));
        dto.setCompetidoresMaisPartidas(todosCompetidor(competidores, c -> c.jogos, "partidas"));
        return dto;
    }

    /**
     * Ranking paginado de clubes/jogadores. Jogadores usam estatísticas globais.
     */
    public HallRankingPageDTO obterRankingPaginado(String chaveRaw, int page, int size, String busca) {
        HallRankingChave chave = HallRankingChave.from(chaveRaw);
        int pagina = Math.max(1, page);
        int tamanho = size > 0 ? Math.min(size, 100) : PAGE_SIZE_DEFAULT;
        String termo = busca == null || busca.isBlank() ? null : busca.trim();
        Pageable pageable = PageRequest.of(pagina - 1, tamanho);

        return switch (chave) {
            case clubesMaisTitulos -> paginaClubesTitulos(termo, pageable, chave);
            case clubesMaisVices -> paginaAgg(
                    campeonatoResultadoRepository.rankingVices(termo, pageable), chave, "vices");
            case clubesMaisFinais -> paginaLista(listarClubesFinais(termo), pagina, tamanho, chave, "finais");
            case clubesMaiorSequenciaTitulos -> paginaLista(
                    topClubesSequenciaTitulosFiltrado(termo), pagina, tamanho, chave, "em sequência");
            case clubesMaisParticipacoes -> paginaAgg(
                    historicoClubeRepository.rankingParticipacoes(termo, pageable), chave, "participações");
            case clubesMelhorAproveitamento -> paginaClubesAproveitamento(termo, pageable, chave);
            case jogadoresMaisGols -> paginaAtletaGlobal(
                    estatisticaAtletaRepository.rankingGlobalGols(termo, pageable), chave, "gols");
            case jogadoresMaisAssistencias -> paginaAtletaGlobal(
                    estatisticaAtletaRepository.rankingGlobalAssistencias(termo, pageable), chave, "assistências");
            case jogadoresMaisGolsContra -> paginaAtletaGlobal(
                    estatisticaAtletaRepository.rankingGlobalGolsContra(termo, pageable), chave, "gols contra");
            case jogadoresMaisAmarelos -> paginaAtletaGlobal(
                    estatisticaAtletaRepository.rankingGlobalAmarelos(termo, pageable), chave, "amarelos");
            case jogadoresMaisVermelhos -> paginaAtletaGlobal(
                    estatisticaAtletaRepository.rankingGlobalVermelhos(termo, pageable), chave, "vermelhos");
            case jogadoresMaisTitulos -> paginaAtletaGlobal(
                    historicoAtletaRepository.rankingTitulos(termo, pageable), chave, "títulos");
        };
    }

    public HallClubePerfilDTO obterPerfilClube(Long clubeId) {
        Clube clube = clubeRepository.findByIdComEstatistica(clubeId)
                .orElseThrow(() -> new CampeonatoBusinessException("Clube não encontrado."));

        List<HistoricoClubeCampeonato> historicos = historicoClubeRepository.findByClubeIdOrderByCampeonatoDesc(clubeId);
        Set<String> vicesKeys = montarChavesVice();

        HallClubePerfilDTO dto = new HallClubePerfilDTO();
        dto.setClubeId(clube.getClubeId());
        dto.setNome(clube.getNome());
        dto.setSigla(clube.getSigla());
        dto.setPais(clube.getPais());
        dto.setRankAtual(clube.getRank() != null ? clube.getRank().getSigla() : null);

        int titulos = 0;
        int vices = 0;
        int jogos = 0;
        int vitorias = 0;
        int empates = 0;
        int derrotas = 0;
        int gp = 0;
        int gc = 0;
        List<HallTimelineClubeItemDTO> timeline = new ArrayList<>();

        for (HistoricoClubeCampeonato h : historicos) {
            Campeonato camp = h.getCampeonato();
            boolean titulo = Boolean.TRUE.equals(h.getTituloConquistado());
            boolean vice = vicesKeys.contains(chaveResultado(camp.getCampeonatoId(), clubeId));
            if (titulo) {
                titulos++;
            }
            if (vice) {
                vices++;
            }
            jogos += valor(h.getJogos());
            vitorias += valor(h.getVitorias());
            empates += valor(h.getEmpates());
            derrotas += valor(h.getDerrotas());
            gp += valor(h.getGolsPro());
            gc += valor(h.getGolsContra());

            HallTimelineClubeItemDTO item = new HallTimelineClubeItemDTO();
            item.setCampeonatoId(camp.getCampeonatoId());
            item.setCampeonatoNome(camp.getNome());
            item.setDataFinalizacao(camp.getDataFinalizacao());
            item.setAno(anoDe(camp.getDataFinalizacao() != null ? camp.getDataFinalizacao() : camp.getDataCriacao()));
            item.setPosicaoFinal(h.getPosicaoFinal());
            item.setTituloConquistado(titulo);
            item.setVice(vice);
            item.setRankInicial(h.getRankAnterior() != null ? h.getRankAnterior().getSigla() : null);
            item.setRankFinal(h.getRankNovo() != null ? h.getRankNovo().getSigla() : null);
            item.setEvolucaoRank(movimentoRank(h.getRankAnterior(), h.getRankNovo()));
            item.setJogos(valor(h.getJogos()));
            item.setVitorias(valor(h.getVitorias()));
            item.setEmpates(valor(h.getEmpates()));
            item.setDerrotas(valor(h.getDerrotas()));
            item.setGolsPro(valor(h.getGolsPro()));
            item.setGolsContra(valor(h.getGolsContra()));
            item.setPontos(valor(h.getVitorias()) * 3 + valor(h.getEmpates()));
            item.setSaldoGols(valor(h.getGolsPro()) - valor(h.getGolsContra()));
            timeline.add(item);
        }

        dto.setTitulos(titulos);
        dto.setVices(vices);
        dto.setParticipacoes(historicos.size());
        dto.setJogos(jogos);
        dto.setVitorias(vitorias);
        dto.setEmpates(empates);
        dto.setDerrotas(derrotas);
        dto.setGolsPro(gp);
        dto.setGolsContra(gc);
        dto.setSaldoGols(gp - gc);
        dto.setAproveitamento(aproveitamento(vitorias, empates, jogos));
        dto.setTimeline(timeline);
        return dto;
    }

    public HallCompetidorPerfilDTO obterPerfilCompetidor(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new CampeonatoBusinessException("Informe o nome do competidor.");
        }
        String chave = normalizar(nome);
        List<CampeonatoResultado> resultados = campeonatoResultadoRepository.findAllComDetalhesOrderByDataDesc();
        List<HistoricoClubeCampeonato> historicos = historicoClubeRepository.findAllComDetalhes();

        HallCompetidorPerfilDTO dto = new HallCompetidorPerfilDTO();
        dto.setNome(nome.trim());

        int titulos = 0;
        int vices = 0;
        int finais = 0;
        int jogos = 0;
        int vitorias = 0;
        int empates = 0;
        int derrotas = 0;
        Set<String> clubes = new LinkedHashSet<>();
        List<HallTimelineCompetidorItemDTO> timeline = new ArrayList<>();
        Set<Long> campeonatosVistos = new HashSet<>();

        for (CampeonatoResultado r : resultados) {
            Campeonato camp = r.getCampeonato();
            if (camp == null || camp.getStatus() != StatusCampeonato.FINALIZADO) {
                continue;
            }
            boolean ehComp1 = nomesIguais(camp.getCompetidor1Nome(), chave);
            boolean ehComp2 = nomesIguais(camp.getCompetidor2Nome(), chave);
            if (!ehComp1 && !ehComp2) {
                continue;
            }
            int numero = ehComp1 ? 1 : 2;
            boolean campeao = nomesIguais(r.getCampeaoCompetidorNome(), chave)
                    || Objects.equals(r.getCampeaoCompetidor(), numero);
            boolean vice = false;
            if (r.getViceCampeaoClube() != null && camp.getClubes() != null) {
                vice = camp.getClubes().stream()
                        .anyMatch(c -> Objects.equals(c.getClubeOrigemId(), r.getViceCampeaoClube().getClubeId())
                                && Objects.equals(c.getCompetidorNumero(), numero));
            }
            if (campeao) {
                titulos++;
                finais++;
            }
            if (vice) {
                vices++;
                finais++;
            }

            HallTimelineCompetidorItemDTO item = new HallTimelineCompetidorItemDTO();
            item.setCampeonatoId(camp.getCampeonatoId());
            item.setCampeonatoNome(camp.getNome());
            item.setDataFinalizacao(camp.getDataFinalizacao());
            item.setAno(anoDe(camp.getDataFinalizacao() != null ? camp.getDataFinalizacao() : r.getDataConquista()));
            item.setCampeao(campeao);
            item.setVice(vice);

            int j = 0;
            int v = 0;
            int e = 0;
            int d = 0;
            List<String> clubesCamp = new ArrayList<>();
            for (HistoricoClubeCampeonato h : historicos) {
                if (!Objects.equals(h.getCampeonato().getCampeonatoId(), camp.getCampeonatoId())) {
                    continue;
                }
                CampeonatoClube snapshot = encontrarSnapshot(camp, h.getClube().getClubeId());
                if (snapshot == null || !Objects.equals(snapshot.getCompetidorNumero(), numero)) {
                    continue;
                }
                clubesCamp.add(h.getClube().getNome());
                clubes.add(h.getClube().getNome());
                j += valor(h.getJogos());
                v += valor(h.getVitorias());
                e += valor(h.getEmpates());
                d += valor(h.getDerrotas());
            }
            item.setClubes(clubesCamp);
            item.setJogos(j);
            item.setVitorias(v);
            item.setEmpates(e);
            item.setDerrotas(d);
            jogos += j;
            vitorias += v;
            empates += e;
            derrotas += d;
            timeline.add(item);
            campeonatosVistos.add(camp.getCampeonatoId());
        }

        dto.setTitulos(titulos);
        dto.setVices(vices);
        dto.setFinais(finais);
        dto.setParticipacoes(campeonatosVistos.size());
        dto.setJogos(jogos);
        dto.setVitorias(vitorias);
        dto.setEmpates(empates);
        dto.setDerrotas(derrotas);
        dto.setAproveitamento(aproveitamento(vitorias, empates, jogos));
        dto.setClubesUtilizados(new ArrayList<>(clubes));
        dto.setTimeline(timeline);
        return dto;
    }

    public HallJogadorPerfilDTO obterPerfilJogador(Long atletaId) {
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new CampeonatoBusinessException("Jogador não encontrado."));

        List<HistoricoAtletaCampeonato> historicos = historicoAtletaRepository.findByAtletaIdOrderByCampeonatoDesc(atletaId);
        HallJogadorPerfilDTO dto = new HallJogadorPerfilDTO();
        dto.setAtletaId(atleta.getAtletaId());
        dto.setNome(atleta.getNome());
        dto.setSobrenome(atleta.getSobrenome());
        dto.setPosicao(atleta.getPosicao() != null ? atleta.getPosicao().toJson() : null);
        if (atleta.getClube() != null) {
            dto.setClubeAtualNome(atleta.getClube().getNome());
        }

        int transferencias = 0;
        int titulos = 0;
        Set<String> clubes = new LinkedHashSet<>();
        List<HallTimelineJogadorItemDTO> timeline = new ArrayList<>();

        // Totais da carreira = estatísticas globais atuais (sincronizam automaticamente)
        int gols = 0;
        int assists = 0;
        int gc = 0;
        int amarelos = 0;
        int vermelhos = 0;
        for (EstatisticaAtleta e : estatisticaAtletaRepository.findAllByAtletaId(atletaId)) {
            gols += valor(e.getGols());
            assists += valor(e.getAssistencias());
            gc += valor(e.getGolsContra());
            amarelos += valor(e.getCartaoAmarelo());
            vermelhos += valor(e.getCartaoVermelho());
            if (e.getClube() != null && e.getClube().getNome() != null) {
                clubes.add(e.getClube().getNome());
            }
        }

        for (HistoricoAtletaCampeonato h : historicos) {
            Campeonato camp = h.getCampeonato();
            transferencias += valor(h.getTransferencias());
            if (Boolean.TRUE.equals(h.getTituloConquistado())) {
                titulos++;
            }
            if (h.getClubesDefendidos() != null && !h.getClubesDefendidos().isBlank()) {
                for (String c : h.getClubesDefendidos().split("[,;|]")) {
                    String nomeClube = c.trim();
                    if (!nomeClube.isEmpty()) {
                        clubes.add(nomeClube);
                    }
                }
            }

            HallTimelineJogadorItemDTO item = new HallTimelineJogadorItemDTO();
            item.setCampeonatoId(camp.getCampeonatoId());
            item.setCampeonatoNome(camp.getNome());
            item.setDataFinalizacao(camp.getDataFinalizacao());
            item.setAno(anoDe(camp.getDataFinalizacao() != null ? camp.getDataFinalizacao() : camp.getDataCriacao()));
            item.setTituloConquistado(Boolean.TRUE.equals(h.getTituloConquistado()));
            item.setGols(valor(h.getGols()));
            item.setAssistencias(valor(h.getAssistencias()));
            item.setGolsContra(valor(h.getGolsContra()));
            item.setCartoesAmarelos(valor(h.getCartoesAmarelos()));
            item.setCartoesVermelhos(valor(h.getCartoesVermelhos()));
            item.setTransferencias(valor(h.getTransferencias()));
            item.setClubesDefendidos(h.getClubesDefendidos());
            timeline.add(item);
        }

        dto.setGols(gols);
        dto.setAssistencias(assists);
        dto.setGolsContra(gc);
        dto.setCartoesAmarelos(amarelos);
        dto.setCartoesVermelhos(vermelhos);
        dto.setTransferencias(transferencias);
        dto.setTitulos(titulos);
        dto.setCampeonatosDisputados(historicos.size());
        dto.setClubesDefendidos(new ArrayList<>(clubes));
        dto.setTimeline(timeline);
        return dto;
    }

    public HallBuscaResultadoDTO buscar(String q) {
        HallBuscaResultadoDTO dto = new HallBuscaResultadoDTO();
        if (q == null || q.isBlank()) {
            return dto;
        }
        String termo = normalizar(q);

        for (HallCampeonatoResumoDTO camp : listarCampeonatos(null, null, null, null, null, null, null)) {
            if (contem(camp.getNome(), termo)
                    || contem(camp.getCampeaoClubeNome(), termo)
                    || contem(camp.getCampeaoCompetidorNome(), termo)) {
                dto.getCampeonatos().add(new HallBuscaItemDTO(
                        "campeonato",
                        camp.getCampeonatoId(),
                        camp.getNome(),
                        camp.getCampeaoClubeNome(),
                        "/hall-da-fama/campeonato/" + camp.getCampeonatoId()));
            }
        }

        Set<Long> clubesComHistorico = historicoClubeRepository.findAllComDetalhes().stream()
                .map(h -> h.getClube().getClubeId())
                .collect(Collectors.toSet());
        for (Clube clube : clubeRepository.findAll()) {
            if (!clubesComHistorico.contains(clube.getClubeId())) {
                continue;
            }
            if (contem(clube.getNome(), termo) || contem(clube.getSigla(), termo)) {
                dto.getClubes().add(new HallBuscaItemDTO(
                        "clube",
                        clube.getClubeId(),
                        clube.getNome(),
                        clube.getSigla(),
                        "/hall-da-fama/clube/" + clube.getClubeId()));
            }
        }

        Set<String> competidores = new LinkedHashSet<>();
        for (CampeonatoResultado r : campeonatoResultadoRepository.findAllComDetalhesOrderByDataDesc()) {
            Campeonato c = r.getCampeonato();
            if (c == null) {
                continue;
            }
            if (c.getCompetidor1Nome() != null && !c.getCompetidor1Nome().isBlank()) {
                competidores.add(c.getCompetidor1Nome().trim());
            }
            if (c.getCompetidor2Nome() != null && !c.getCompetidor2Nome().isBlank()) {
                competidores.add(c.getCompetidor2Nome().trim());
            }
        }
        for (String nomeComp : competidores) {
            if (contem(nomeComp, termo)) {
                dto.getCompetidores().add(new HallBuscaItemDTO(
                        "competidor",
                        null,
                        nomeComp,
                        "Competidor",
                        "/hall-da-fama/competidor/" + encodePath(nomeComp)));
            }
        }

        Set<Long> atletasComHistorico = historicoAtletaRepository.findAllComDetalhes().stream()
                .map(h -> h.getAtleta().getAtletaId())
                .collect(Collectors.toSet());
        for (Atleta atleta : atletaRepository.findAll()) {
            if (!atletasComHistorico.contains(atleta.getAtletaId())) {
                continue;
            }
            String nomeCompleto = nomeAtleta(atleta.getNome(), atleta.getSobrenome());
            if (contem(nomeCompleto, termo) || contem(atleta.getNome(), termo) || contem(atleta.getSobrenome(), termo)) {
                dto.getJogadores().add(new HallBuscaItemDTO(
                        "jogador",
                        atleta.getAtletaId(),
                        nomeCompleto,
                        atleta.getPosicao() != null ? atleta.getPosicao().getDescricaoPosicao() : null,
                        "/hall-da-fama/jogador/" + atleta.getAtletaId()));
            }
        }

        limitar(dto.getCampeonatos(), TOP);
        limitar(dto.getClubes(), TOP);
        limitar(dto.getCompetidores(), TOP);
        limitar(dto.getJogadores(), TOP);
        return dto;
    }

    // -------------------------------------------------------------------------
    // Montagem / filtros
    // -------------------------------------------------------------------------

    private HallCampeonatoResumoDTO toResumo(
            CampeonatoResultado resultado,
            Campeonato campeonato,
            CampeonatoEstatisticasDTO stats) {

        HallCampeonatoResumoDTO dto = new HallCampeonatoResumoDTO();
        dto.setCampeonatoId(campeonato.getCampeonatoId());
        dto.setNome(resultado.getNomeCampeonato());
        dto.setDataCriacao(campeonato.getDataCriacao());
        dto.setDataFinalizacao(resultado.getDataConquista());
        dto.setQuantidadeClubes(resultado.getQuantidadeParticipantes());

        int competidores = 0;
        if (campeonato.getCompetidor1Nome() != null && !campeonato.getCompetidor1Nome().isBlank()) {
            competidores++;
        }
        if (campeonato.getCompetidor2Nome() != null && !campeonato.getCompetidor2Nome().isBlank()) {
            competidores++;
        }
        dto.setQuantidadeCompetidores(competidores);
        dto.setCompetidor1Nome(campeonato.getCompetidor1Nome());
        dto.setCompetidor2Nome(campeonato.getCompetidor2Nome());

        if (resultado.getCampeaoClube() != null) {
            dto.setCampeaoClubeId(resultado.getCampeaoClube().getClubeId());
            dto.setCampeaoClubeNome(resultado.getCampeaoClube().getNome());
        }
        dto.setCampeaoCompetidor(resultado.getCampeaoCompetidor());
        dto.setCampeaoCompetidorNome(resultado.getCampeaoCompetidorNome());
        if (resultado.getViceCampeaoClube() != null) {
            dto.setViceCampeaoClubeId(resultado.getViceCampeaoClube().getClubeId());
            dto.setViceCampeaoClubeNome(resultado.getViceCampeaoClube().getNome());
        }

        if (stats != null && stats.getDashboard() != null) {
            dto.setQuantidadePartidas(stats.getDashboard().getQuantidadePartidas());
            dto.setQuantidadeGols(stats.getDashboard().getQuantidadeGols());
            dto.setQuantidadeCartoes(stats.getDashboard().getQuantidadeCartoes());
        }

        if (campeonato.getDataCriacao() != null && resultado.getDataConquista() != null) {
            long minutos = Duration.between(campeonato.getDataCriacao(), resultado.getDataConquista()).toMinutes();
            if (minutos >= 0) {
                dto.setDuracaoMinutos(minutos);
                dto.setDuracaoFormatada(formatarDuracao(minutos));
            }
        }
        return dto;
    }

    private boolean passarFiltros(
            HallCampeonatoResumoDTO dto,
            Campeonato campeonato,
            CampeonatoResultado resultado,
            String busca,
            Integer ano,
            Integer quantidadeClubes,
            String competidor,
            String clube,
            String campeao,
            String ranking) {

        if (ano != null) {
            Integer a = anoDe(resultado.getDataConquista());
            if (!Objects.equals(a, ano)) {
                return false;
            }
        }
        if (quantidadeClubes != null && !Objects.equals(dto.getQuantidadeClubes(), quantidadeClubes)) {
            return false;
        }
        if (competidor != null && !competidor.isBlank()) {
            String c = normalizar(competidor);
            if (!contem(campeonato.getCompetidor1Nome(), c)
                    && !contem(campeonato.getCompetidor2Nome(), c)
                    && !contem(resultado.getCampeaoCompetidorNome(), c)) {
                return false;
            }
        }
        if (clube != null && !clube.isBlank()) {
            String c = normalizar(clube);
            boolean match = contem(dto.getCampeaoClubeNome(), c) || contem(dto.getViceCampeaoClubeNome(), c);
            if (!match && campeonato.getClubes() != null) {
                match = campeonato.getClubes().stream()
                        .anyMatch(cc -> contem(cc.getNome(), c) || contem(cc.getSigla(), c));
            }
            if (!match) {
                return false;
            }
        }
        if (campeao != null && !campeao.isBlank()) {
            String c = normalizar(campeao);
            if (!contem(dto.getCampeaoClubeNome(), c) && !contem(dto.getCampeaoCompetidorNome(), c)) {
                return false;
            }
        }
        if (ranking != null && !ranking.isBlank()) {
            String r = ranking.trim().toUpperCase(Locale.ROOT);
            List<HistoricoClubeCampeonato> hist = historicoClubeRepository
                    .findByCampeonatoCampeonatoId(campeonato.getCampeonatoId());
            boolean match = hist.stream().anyMatch(h ->
                    (h.getRankAnterior() != null && r.equalsIgnoreCase(h.getRankAnterior().getSigla()))
                            || (h.getRankNovo() != null && r.equalsIgnoreCase(h.getRankNovo().getSigla())));
            if (!match) {
                return false;
            }
        }
        if (busca != null && !busca.isBlank()) {
            String b = normalizar(busca);
            if (!contem(dto.getNome(), b)
                    && !contem(dto.getCampeaoClubeNome(), b)
                    && !contem(dto.getCampeaoCompetidorNome(), b)
                    && !contem(dto.getViceCampeaoClubeNome(), b)
                    && !contem(dto.getCompetidor1Nome(), b)
                    && !contem(dto.getCompetidor2Nome(), b)) {
                return false;
            }
        }
        return true;
    }

    private void enriquecerRanksClassificacao(Long campeonatoId, CampeonatoEstatisticasDTO estatisticas) {
        Map<Long, HistoricoClubeCampeonato> porClubeOrigem = historicoClubeRepository
                .findByCampeonatoCampeonatoId(campeonatoId).stream()
                .collect(Collectors.toMap(h -> h.getClube().getClubeId(), h -> h, (a, b) -> a));

        for (ClassificacaoClubeDTO row : estatisticas.getClassificacao()) {
            if (row.getClubeOrigemId() == null) {
                continue;
            }
            HistoricoClubeCampeonato h = porClubeOrigem.get(row.getClubeOrigemId());
            if (h == null) {
                continue;
            }
            String inicial = h.getRankAnterior() != null ? h.getRankAnterior().getSigla() : null;
            String fim = h.getRankNovo() != null ? h.getRankNovo().getSigla() : null;
            row.setRankInicial(inicial);
            row.setRankFinal(fim);
            row.setEvolucaoRank(movimentoRank(h.getRankAnterior(), h.getRankNovo()));
        }
    }

    private List<RankingAtletaCampeonatoDTO> montarRankingGolsContra(List<CampeonatoAtleta> atletas) {
        Map<String, List<CampeonatoAtleta>> porIdentidade = new LinkedHashMap<>();
        for (CampeonatoAtleta atleta : atletas) {
            String identidade = CampeonatoAtletaIdentidade.garantir(atleta);
            porIdentidade.computeIfAbsent(identidade, k -> new ArrayList<>()).add(atleta);
        }

        List<RankingAtletaCampeonatoDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, List<CampeonatoAtleta>> entry : porIdentidade.entrySet()) {
            List<CampeonatoAtleta> vinculos = entry.getValue();
            CampeonatoAtleta ativo = vinculos.stream()
                    .filter(CampeonatoAtleta::isAtivo)
                    .findFirst()
                    .orElse(vinculos.get(0));

            int golsContra = 0;
            for (CampeonatoAtleta v : vinculos) {
                golsContra += valor(v.getGolsContra());
            }
            if (golsContra <= 0) {
                continue;
            }

            RankingAtletaCampeonatoDTO dto = new RankingAtletaCampeonatoDTO();
            dto.setIdentidade(entry.getKey());
            dto.setCampeonatoAtletaId(ativo.getCampeonatoAtletaId());
            dto.setNome(ativo.getNome());
            dto.setSobrenome(ativo.getSobrenome());
            if (ativo.getCampeonatoClube() != null) {
                dto.setCampeonatoClubeId(ativo.getCampeonatoClube().getCampeonatoClubeId());
                dto.setClubeNome(ativo.getCampeonatoClube().getNome());
            }
            dto.setGolsContra(golsContra);
            resultado.add(dto);
        }

        resultado.sort(Comparator
                .comparing((RankingAtletaCampeonatoDTO a) -> valor(a.getGolsContra()), Comparator.reverseOrder())
                .thenComparing(a -> nomeAtleta(a.getNome(), a.getSobrenome()), String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    private List<RankingAtletaCampeonatoDTO> montarGoleirosMenosVazados(
            List<CampeonatoAtleta> atletas,
            CampeonatoEstatisticasDTO stats) {

        Map<Long, Integer> gcPorClube = stats.getClassificacao().stream()
                .filter(c -> c.getCampeonatoClubeId() != null)
                .collect(Collectors.toMap(
                        ClassificacaoClubeDTO::getCampeonatoClubeId,
                        c -> valor(c.getGolsContra()),
                        (a, b) -> a));

        Map<String, RankingAtletaCampeonatoDTO> porIdentidade = new LinkedHashMap<>();
        for (CampeonatoAtleta a : atletas) {
            if (a.getPosicao() != PosicaoFutebol.GOLEIRO || !a.isAtivo()) {
                continue;
            }
            String identidade = CampeonatoAtletaIdentidade.garantir(a);
            RankingAtletaCampeonatoDTO dto = new RankingAtletaCampeonatoDTO();
            dto.setCampeonatoAtletaId(a.getCampeonatoAtletaId());
            dto.setIdentidade(identidade);
            dto.setNome(a.getNome());
            dto.setSobrenome(a.getSobrenome());
            if (a.getCampeonatoClube() != null) {
                dto.setCampeonatoClubeId(a.getCampeonatoClube().getCampeonatoClubeId());
                dto.setClubeNome(a.getCampeonatoClube().getNome());
                dto.setGolsContra(gcPorClube.getOrDefault(a.getCampeonatoClube().getCampeonatoClubeId(), 0));
            } else {
                dto.setGolsContra(0);
            }
            porIdentidade.putIfAbsent(identidade, dto);
        }

        return porIdentidade.values().stream()
                .sorted(Comparator
                        .comparing((RankingAtletaCampeonatoDTO a) -> valor(a.getGolsContra()))
                        .thenComparing(a -> nomeAtleta(a.getNome(), a.getSobrenome()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private void preencherExtremosClubes(HallCampeonatoDetalheDTO detalhe, CampeonatoEstatisticasDTO stats) {
        List<ClassificacaoClubeDTO> comJogos = stats.getClassificacao().stream()
                .filter(c -> valor(c.getJogos()) > 0)
                .collect(Collectors.toList());
        comJogos.stream().min(Comparator.comparingInt(c -> valor(c.getGolsPro()))).ifPresent(detalhe::setPiorAtaque);
        comJogos.stream().max(Comparator.comparingInt(c -> valor(c.getGolsContra()))).ifPresent(detalhe::setPiorDefesa);
        comJogos.stream().min(Comparator.comparingInt(c -> valor(c.getSaldoGols()))).ifPresent(detalhe::setMenorSaldo);
    }

    private HallGoleadaDTO calcularMaiorGoleada(Campeonato campeonato) {
        HallGoleadaDTO melhor = null;
        int maiorDiff = -1;
        if (campeonato.getRodadas() == null) {
            return null;
        }
        for (CampeonatoRodada rodada : campeonato.getRodadas()) {
            if (rodada.getPartidas() == null) {
                continue;
            }
            for (CampeonatoPartida partida : rodada.getPartidas()) {
                if (partida.getStatus() != StatusPartida.FINALIZADA) {
                    continue;
                }
                int gm = valor(partida.getGolsMandante());
                int gv = valor(partida.getGolsVisitante());
                int diff = Math.abs(gm - gv);
                if (diff > maiorDiff) {
                    maiorDiff = diff;
                    melhor = new HallGoleadaDTO();
                    melhor.setPartidaId(partida.getCampeonatoPartidaId());
                    melhor.setGolsMandante(gm);
                    melhor.setGolsVisitante(gv);
                    melhor.setDiferenca(diff);
                    melhor.setPlacar(gm + " × " + gv);
                    melhor.setRodadaNome(rodada.getNome());
                    if (partida.getClubeMandante() != null) {
                        melhor.setMandanteNome(partida.getClubeMandante().getNome());
                    }
                    if (partida.getClubeVisitante() != null) {
                        melhor.setVisitanteNome(partida.getClubeVisitante().getNome());
                    }
                }
            }
        }
        return melhor;
    }

    private void preencherSequencias(HallCampeonatoDetalheDTO detalhe, Campeonato campeonato) {
        Map<Long, List<String>> resultadosPorClube = new HashMap<>();
        Map<Long, String> nomes = new HashMap<>();

        if (campeonato.getRodadas() == null) {
            return;
        }

        List<CampeonatoRodada> rodadas = campeonato.getRodadas().stream()
                .sorted(Comparator.comparing(r -> r.getNumeroRodada() == null ? 0 : r.getNumeroRodada()))
                .collect(Collectors.toList());

        for (CampeonatoRodada rodada : rodadas) {
            if (rodada.getPartidas() == null) {
                continue;
            }
            List<CampeonatoPartida> partidas = rodada.getPartidas().stream()
                    .sorted(Comparator.comparing(p -> p.getOrdem() == null ? 0 : p.getOrdem()))
                    .collect(Collectors.toList());

            for (CampeonatoPartida partida : partidas) {
                if (partida.getStatus() != StatusPartida.FINALIZADA || partida.getClubeVencedor() == null) {
                    continue;
                }
                Long vencedorId = partida.getClubeVencedor().getCampeonatoClubeId();
                registrarResultado(resultadosPorClube, nomes, partida.getClubeMandante(),
                        partida.getClubeMandante() != null
                                && Objects.equals(partida.getClubeMandante().getCampeonatoClubeId(), vencedorId)
                                        ? "V" : "D");
                registrarResultado(resultadosPorClube, nomes, partida.getClubeVisitante(),
                        partida.getClubeVisitante() != null
                                && Objects.equals(partida.getClubeVisitante().getCampeonatoClubeId(), vencedorId)
                                        ? "V" : "D");
            }
        }

        HallSequenciaDTO melhorV = null;
        HallSequenciaDTO melhorI = null;
        for (Map.Entry<Long, List<String>> entry : resultadosPorClube.entrySet()) {
            int seqV = maiorSequencia(entry.getValue(), Set.of("V"));
            int seqI = maiorSequencia(entry.getValue(), Set.of("V", "E"));
            Long origem = entry.getKey();
            String nome = nomes.get(origem);
            if (melhorV == null || seqV > melhorV.getSequencia()) {
                melhorV = new HallSequenciaDTO(origem, nome, seqV, "VITORIAS");
            }
            if (melhorI == null || seqI > melhorI.getSequencia()) {
                melhorI = new HallSequenciaDTO(origem, nome, seqI, "INVICTO");
            }
        }
        detalhe.setMaiorSequenciaVitorias(melhorV);
        detalhe.setMaiorSequenciaSemPerder(melhorI);
    }

    private void registrarResultado(
            Map<Long, List<String>> mapa,
            Map<Long, String> nomes,
            CampeonatoClube clube,
            String resultado) {
        if (clube == null || clube.getClubeOrigemId() == null) {
            return;
        }
        nomes.put(clube.getClubeOrigemId(), clube.getNome());
        mapa.computeIfAbsent(clube.getClubeOrigemId(), k -> new ArrayList<>()).add(resultado);
    }

    private int maiorSequencia(List<String> resultados, Set<String> validos) {
        int melhor = 0;
        int atual = 0;
        for (String r : resultados) {
            if (validos.contains(r)) {
                atual++;
                melhor = Math.max(melhor, atual);
            } else {
                atual = 0;
            }
        }
        return melhor;
    }

    // -------------------------------------------------------------------------
    // Recordes
    // -------------------------------------------------------------------------

    private Map<String, CompetidorAgg> agregarCompetidores(
            List<CampeonatoResultado> resultados,
            List<HistoricoClubeCampeonato> historicos) {

        Map<String, CompetidorAgg> map = new HashMap<>();

        for (CampeonatoResultado r : resultados) {
            Campeonato camp = r.getCampeonato();
            if (camp == null) {
                continue;
            }
            registrarCompetidorBase(map, camp.getCompetidor1Nome());
            registrarCompetidorBase(map, camp.getCompetidor2Nome());

            if (r.getCampeaoCompetidorNome() != null && !r.getCampeaoCompetidorNome().isBlank()) {
                CompetidorAgg a = map.computeIfAbsent(normalizar(r.getCampeaoCompetidorNome()),
                        k -> new CompetidorAgg(r.getCampeaoCompetidorNome().trim()));
                a.titulos++;
                a.finais++;
            }
            if (r.getViceCampeaoClube() != null && camp.getClubes() != null) {
                camp.getClubes().stream()
                        .filter(c -> Objects.equals(c.getClubeOrigemId(), r.getViceCampeaoClube().getClubeId()))
                        .findFirst()
                        .ifPresent(snap -> {
                            String nomeComp = Objects.equals(snap.getCompetidorNumero(), 1)
                                    ? camp.getCompetidor1Nome()
                                    : camp.getCompetidor2Nome();
                            if (nomeComp != null && !nomeComp.isBlank()) {
                                CompetidorAgg a = map.computeIfAbsent(normalizar(nomeComp),
                                        k -> new CompetidorAgg(nomeComp.trim()));
                                a.vices++;
                                a.finais++;
                            }
                        });
            }
        }

        for (HistoricoClubeCampeonato h : historicos) {
            Campeonato camp = h.getCampeonato();
            CampeonatoClube snap = encontrarSnapshot(camp, h.getClube().getClubeId());
            if (snap == null) {
                continue;
            }
            String nomeComp = Objects.equals(snap.getCompetidorNumero(), 1)
                    ? camp.getCompetidor1Nome()
                    : camp.getCompetidor2Nome();
            if (nomeComp == null || nomeComp.isBlank()) {
                continue;
            }
            CompetidorAgg a = map.computeIfAbsent(normalizar(nomeComp), k -> new CompetidorAgg(nomeComp.trim()));
            a.jogos += valor(h.getJogos());
            a.vitorias += valor(h.getVitorias());
            a.empates += valor(h.getEmpates());
        }
        return map;
    }

    private void registrarCompetidorBase(Map<String, CompetidorAgg> map, String nome) {
        if (nome == null || nome.isBlank()) {
            return;
        }
        map.computeIfAbsent(normalizar(nome), k -> new CompetidorAgg(nome.trim()));
    }

    private List<HallRecordeItemDTO> todosCompetidor(
            Map<String, CompetidorAgg> map,
            ToIntFunction<CompetidorAgg> extractor,
            String label) {
        return map.values().stream()
                .filter(c -> extractor.applyAsInt(c) > 0)
                .sorted(Comparator.comparingInt(extractor).reversed()
                        .thenComparing(c -> c.nome, String.CASE_INSENSITIVE_ORDER))
                .map(c -> new HallRecordeItemDTO(null, c.nome, null, extractor.applyAsInt(c), label))
                .collect(Collectors.toList());
    }

    private List<HallRecordeItemDTO> todosCompetidorAproveitamento(Map<String, CompetidorAgg> map) {
        return map.values().stream()
                .filter(c -> c.jogos >= 3)
                .map(c -> new HallRecordeItemDTO(null, c.nome, null,
                        aproveitamento(c.vitorias, c.empates, c.jogos), "%"))
                .sorted(Comparator.comparing((HallRecordeItemDTO i) -> i.getValor().doubleValue()).reversed())
                .collect(Collectors.toList());
    }

    private HallRankingPageDTO paginaClubesTitulos(String busca, Pageable pageable, HallRankingChave chave) {
        Page<Object[]> page = estatisticaClubeRepository.rankingPorTitulos(busca, pageable);
        List<HallRecordeItemDTO> itens = page.getContent().stream()
                .map(row -> new HallRecordeItemDTO(
                        toLong(row[0]),
                        row[1] != null ? row[1].toString() : null,
                        row[2] != null ? row[2].toString() : null,
                        toLong(row[3]),
                        "títulos"))
                .collect(Collectors.toList());
        return toPageDto(chave, itens, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    private HallRankingPageDTO paginaClubesAproveitamento(String busca, Pageable pageable, HallRankingChave chave) {
        Page<Object[]> page = estatisticaClubeRepository.rankingPorAproveitamento(busca, pageable);
        List<HallRecordeItemDTO> itens = page.getContent().stream()
                .map(row -> {
                    int v = toInt(row[3]);
                    int e = toInt(row[4]);
                    int d = toInt(row[5]);
                    int j = v + e + d;
                    return new HallRecordeItemDTO(
                            toLong(row[0]),
                            row[1] != null ? row[1].toString() : null,
                            row[2] != null ? row[2].toString() : null,
                            aproveitamento(v, e, j),
                            "%");
                })
                .collect(Collectors.toList());
        return toPageDto(chave, itens, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    private HallRankingPageDTO paginaAgg(Page<Object[]> page, HallRankingChave chave, String label) {
        List<HallRecordeItemDTO> itens = page.getContent().stream()
                .map(row -> new HallRecordeItemDTO(
                        toLong(row[0]),
                        row[1] != null ? row[1].toString() : null,
                        null,
                        toLong(row[2]),
                        label))
                .collect(Collectors.toList());
        return toPageDto(chave, itens, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    private HallRankingPageDTO paginaAtletaGlobal(Page<Object[]> page, HallRankingChave chave, String label) {
        List<HallRecordeItemDTO> itens = page.getContent().stream()
                .map(row -> {
                    Long id = toLong(row[0]);
                    String nome = nomeAtleta(
                            row[1] != null ? row[1].toString() : null,
                            row[2] != null ? row[2].toString() : null);
                    Number valor = row[3] instanceof Number n ? n : toLong(row[3]);
                    return new HallRecordeItemDTO(id, nome, null, valor, label);
                })
                .collect(Collectors.toList());
        return toPageDto(chave, itens, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    private HallRankingPageDTO paginaLista(
            List<HallRecordeItemDTO> completa,
            int pagina,
            int tamanho,
            HallRankingChave chave,
            String labelIgnorado) {
        int from = Math.max(0, (pagina - 1) * tamanho);
        int to = Math.min(completa.size(), from + tamanho);
        List<HallRecordeItemDTO> slice = from >= completa.size()
                ? List.of()
                : completa.subList(from, to);
        return toPageDto(chave, slice, pagina, tamanho, completa.size());
    }

    private List<HallRecordeItemDTO> listarClubesFinais(String busca) {
        String termo = busca == null ? "" : normalizar(busca);
        Map<Long, AggNome> map = new HashMap<>();
        for (CampeonatoResultado r : campeonatoResultadoRepository.findAllComDetalhesOrderByDataDesc()) {
            if (r.getCampeaoClube() != null) {
                Clube c = r.getCampeaoClube();
                if (termo.isEmpty() || contem(c.getNome(), termo)) {
                    map.computeIfAbsent(c.getClubeId(), id -> new AggNome(id, c.getNome())).valor++;
                }
            }
            if (r.getViceCampeaoClube() != null) {
                Clube c = r.getViceCampeaoClube();
                if (termo.isEmpty() || contem(c.getNome(), termo)) {
                    map.computeIfAbsent(c.getClubeId(), id -> new AggNome(id, c.getNome())).valor++;
                }
            }
        }
        return map.values().stream()
                .filter(a -> a.valor > 0)
                .sorted(Comparator.comparingInt((AggNome a) -> a.valor).reversed()
                        .thenComparing(a -> a.nome, String.CASE_INSENSITIVE_ORDER))
                .map(a -> new HallRecordeItemDTO(a.id, a.nome, null, a.valor, "finais"))
                .collect(Collectors.toList());
    }

    private List<HallRecordeItemDTO> topClubesSequenciaTitulosFiltrado(String busca) {
        String termo = busca == null ? "" : normalizar(busca);
        List<HallRecordeItemDTO> todos = topClubesSequenciaTitulosCompleto(
                campeonatoResultadoRepository.findAllComDetalhesOrderByDataDesc());
        if (termo.isEmpty()) {
            return todos;
        }
        return todos.stream()
                .filter(i -> contem(i.getNome(), termo))
                .collect(Collectors.toList());
    }

    private List<HallRecordeItemDTO> topClubesSequenciaTitulosCompleto(List<CampeonatoResultado> resultados) {
        Map<Long, List<Boolean>> porClube = new HashMap<>();
        Map<Long, String> nomes = new HashMap<>();
        List<CampeonatoResultado> cronologico = new ArrayList<>(resultados);
        cronologico.sort(Comparator.comparing(CampeonatoResultado::getDataConquista,
                Comparator.nullsLast(Comparator.naturalOrder())));

        Set<Long> todosClubes = new HashSet<>();
        for (CampeonatoResultado r : cronologico) {
            if (r.getCampeaoClube() != null) {
                todosClubes.add(r.getCampeaoClube().getClubeId());
                nomes.put(r.getCampeaoClube().getClubeId(), r.getCampeaoClube().getNome());
            }
        }

        for (CampeonatoResultado r : cronologico) {
            Long campeaoId = r.getCampeaoClube() != null ? r.getCampeaoClube().getClubeId() : null;
            for (Long clubeId : todosClubes) {
                porClube.computeIfAbsent(clubeId, k -> new ArrayList<>())
                        .add(Objects.equals(clubeId, campeaoId));
            }
        }

        List<HallRecordeItemDTO> lista = new ArrayList<>();
        for (Map.Entry<Long, List<Boolean>> e : porClube.entrySet()) {
            int melhor = 0;
            int atual = 0;
            for (Boolean titulo : e.getValue()) {
                if (Boolean.TRUE.equals(titulo)) {
                    atual++;
                    melhor = Math.max(melhor, atual);
                } else {
                    atual = 0;
                }
            }
            if (melhor > 0) {
                lista.add(new HallRecordeItemDTO(e.getKey(), nomes.get(e.getKey()), null, melhor, "em sequência"));
            }
        }
        lista.sort(Comparator.comparingInt((HallRecordeItemDTO i) -> i.getValor().intValue()).reversed()
                .thenComparing(HallRecordeItemDTO::getNome, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    private HallRankingPageDTO toPageDto(
            HallRankingChave chave,
            List<HallRecordeItemDTO> itens,
            int page,
            int size,
            long totalElements) {
        HallRankingPageDTO dto = new HallRankingPageDTO();
        dto.setChave(chave.name());
        dto.setItens(itens);
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalElements(totalElements);
        dto.setTotalPages(size <= 0 ? 0 : (int) Math.ceil(totalElements / (double) size));
        return dto;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Set<String> montarChavesVice() {
        Set<String> keys = new HashSet<>();
        for (CampeonatoResultado r : campeonatoResultadoRepository.findAllComDetalhesOrderByDataDesc()) {
            if (r.getViceCampeaoClube() != null && r.getCampeonato() != null) {
                keys.add(chaveResultado(r.getCampeonato().getCampeonatoId(), r.getViceCampeaoClube().getClubeId()));
            }
        }
        return keys;
    }

    private String chaveResultado(Long campeonatoId, Long clubeId) {
        return campeonatoId + ":" + clubeId;
    }

    private CampeonatoClube encontrarSnapshot(Campeonato campeonato, Long clubeOrigemId) {
        if (campeonato == null || campeonato.getClubes() == null || clubeOrigemId == null) {
            return null;
        }
        return campeonato.getClubes().stream()
                .filter(c -> Objects.equals(c.getClubeOrigemId(), clubeOrigemId))
                .findFirst()
                .orElse(null);
    }

    private String movimentoRank(ClubRank rankInicial, ClubRank rankFinal) {
        if (rankInicial == null || rankFinal == null) {
            return "PERMANECEU";
        }
        int diff = rankFinal.getNivel() - rankInicial.getNivel();
        if (diff < 0) {
            return "PROMOCAO";
        }
        if (diff > 0) {
            return "REBAIXAMENTO";
        }
        return "PERMANECEU";
    }

    private int valor(Integer value) {
        return value == null ? 0 : value;
    }

    private String normalizar(String s) {
        if (s == null) {
            return "";
        }
        String nfd = Normalizer.normalize(s, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT).trim();
    }

    private boolean contem(String texto, String termoNormalizado) {
        if (texto == null || termoNormalizado == null || termoNormalizado.isEmpty()) {
            return false;
        }
        return normalizar(texto).contains(termoNormalizado);
    }

    private boolean nomesIguais(String nome, String chaveNormalizada) {
        if (nome == null || chaveNormalizada == null || chaveNormalizada.isEmpty()) {
            return false;
        }
        return normalizar(nome).equals(chaveNormalizada);
    }

    private String nomeAtleta(String nome, String sobrenome) {
        if (nome == null && sobrenome == null) {
            return "";
        }
        if (sobrenome == null || sobrenome.isBlank()) {
            return nome == null ? "" : nome;
        }
        if (nome == null || nome.isBlank()) {
            return sobrenome;
        }
        return nome + " " + sobrenome;
    }

    private Integer anoDe(LocalDateTime data) {
        return data == null ? null : data.getYear();
    }

    private String formatarDuracao(long minutos) {
        if (minutos < 60) {
            return minutos + " min";
        }
        long horas = minutos / 60;
        long resto = minutos % 60;
        if (horas < 24) {
            return resto == 0 ? horas + " h" : horas + " h " + resto + " min";
        }
        long dias = horas / 24;
        long horasResto = horas % 24;
        if (horasResto == 0) {
            return dias + (dias == 1 ? " dia" : " dias");
        }
        return dias + (dias == 1 ? " dia" : " dias") + " " + horasResto + " h";
    }

    private double aproveitamento(int vitorias, int empates, int jogos) {
        if (jogos <= 0) {
            return 0.0;
        }
        double pontos = vitorias * 3.0 + empates;
        double max = jogos * 3.0;
        return Math.round((pontos / max) * 1000.0) / 10.0;
    }

    private String encodePath(String nome) {
        return URLEncoder.encode(nome, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private void limitar(List<HallBuscaItemDTO> lista, int max) {
        if (lista.size() > max) {
            lista.subList(max, lista.size()).clear();
        }
    }

    // -------------------------------------------------------------------------
    // Agregadores internos
    // -------------------------------------------------------------------------

    private static final class AggNome {
        final Long id;
        final String nome;
        int valor;

        AggNome(Long id, String nome) {
            this.id = id;
            this.nome = nome != null ? nome : "";
        }
    }

    private static final class CompetidorAgg {
        final String nome;
        int titulos;
        int vices;
        int finais;
        int jogos;
        int vitorias;
        int empates;

        CompetidorAgg(String nome) {
            this.nome = nome != null ? nome : "";
        }
    }
}
