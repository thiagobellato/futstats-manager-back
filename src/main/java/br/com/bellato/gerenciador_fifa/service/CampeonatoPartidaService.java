package br.com.bellato.gerenciador_fifa.service;

import java.util.ArrayList;
import java.util.Comparator;
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

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoAtletaPartidaDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoDashboardDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoEstatisticasDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClassificacaoClubeDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.DefinirVencedorRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.PartidaDetalheResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.PartidaEventoRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.PartidaEventoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RankingAtletaCampeonatoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RegistrarPartidaRequestDTO;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartidaEvento;
import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoAtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoPartidaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoRepository;
import br.com.bellato.gerenciador_fifa.service.transferencia.CampeonatoAtletaIdentidade;
import br.com.bellato.gerenciador_fifa.validator.PartidaRegistroValidator;

@Service
public class CampeonatoPartidaService {

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private CampeonatoPartidaRepository campeonatoPartidaRepository;

    @Autowired
    private CampeonatoAtletaRepository campeonatoAtletaRepository;

    @Autowired
    private CampeonatoMotorService campeonatoMotorService;

    @Autowired
    private CampeonatoSuspensaoService campeonatoSuspensaoService;

    @Transactional(readOnly = true)
    public PartidaDetalheResponseDTO obterDetalhe(Long campeonatoId, Long partidaId) {
        CampeonatoPartida partida = obterPartidaDoCampeonato(campeonatoId, partidaId);
        return toDetalheDTO(campeonatoId, partida);
    }

    @Transactional
    public Campeonato registrarResultado(Long campeonatoId, Long partidaId, RegistrarPartidaRequestDTO request) {
        PartidaRegistroValidator.validarRequestBasico(request);

        CampeonatoPartida partida = obterPartidaDoCampeonato(campeonatoId, partidaId);

        if (partida.getStatus() == StatusPartida.FINALIZADA) {
            throw new CampeonatoBusinessException("Esta partida já foi finalizada e não pode ser alterada.");
        }

        List<PartidaEventoRequestDTO> eventosRequest = request.getEventos() == null
                ? List.of()
                : request.getEventos();

        List<CampeonatoAtleta> atletasResolvidos = resolverAtletas(campeonatoId, eventosRequest);
        Set<String> suspensos = campeonatoSuspensaoService.identidadesSuspensasAtivas(campeonatoId);
        PartidaRegistroValidator.validarAtletasSuspensos(atletasResolvidos, suspensos);
        PartidaRegistroValidator.validarEventosContraPlacar(partida, request, atletasResolvidos);
        CampeonatoClube vencedor = PartidaRegistroValidator.determinarVencedor(partida, request);

        partida.setGolsMandante(request.getGolsMandante());
        partida.setGolsVisitante(request.getGolsVisitante());

        boolean disputouPenaltis = Boolean.TRUE.equals(request.getDisputouPenaltis())
                && Objects.equals(request.getGolsMandante(), request.getGolsVisitante());
        partida.setDisputouPenaltis(disputouPenaltis);
        if (disputouPenaltis) {
            partida.setPenaltisMandante(request.getPenaltisMandante());
            partida.setPenaltisVisitante(request.getPenaltisVisitante());
        } else {
            partida.setPenaltisMandante(null);
            partida.setPenaltisVisitante(null);
        }

        substituirEventos(partida, eventosRequest, atletasResolvidos);
        campeonatoPartidaRepository.save(partida);

        DefinirVencedorRequestDTO vencedorRequest = new DefinirVencedorRequestDTO();
        vencedorRequest.setCampeonatoClubeId(vencedor.getCampeonatoClubeId());
        Campeonato campeonato = campeonatoMotorService.definirVencedor(campeonatoId, partidaId, vencedorRequest);

        recalcularEstatisticasCampeonato(campeonatoId);
        return campeonato;
    }

    @Transactional
    public void recalcularEstatisticasCampeonato(Long campeonatoId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new CampeonatoBusinessException("Campeonato não encontrado."));

        List<CampeonatoAtleta> atletas = campeonatoAtletaRepository.findByCampeonatoCampeonatoId(campeonatoId);
        for (CampeonatoAtleta atleta : atletas) {
            atleta.setGols(0);
            atleta.setAssistencias(0);
            atleta.setCartoesAmarelos(0);
            atleta.setCartoesVermelhos(0);
            atleta.setGolsContra(0);
            CampeonatoAtletaIdentidade.garantir(atleta);
        }

        Map<Long, CampeonatoAtleta> atletasPorId = atletas.stream()
                .collect(Collectors.toMap(CampeonatoAtleta::getCampeonatoAtletaId, a -> a));

        Map<Long, CampeonatoClube> clubesPorId = new HashMap<>();
        if (campeonato.getClubes() != null) {
            for (CampeonatoClube clube : campeonato.getClubes()) {
                clube.setJogos(0);
                clube.setPontos(0);
                clube.setVitorias(0);
                clube.setEmpates(0);
                clube.setDerrotas(0);
                clube.setGolsPro(0);
                clube.setGolsContra(0);
                clubesPorId.put(clube.getCampeonatoClubeId(), clube);
            }
        }

        if (campeonato.getRodadas() != null) {
            for (CampeonatoRodada rodada : campeonato.getRodadas()) {
                if (rodada.getPartidas() == null) {
                    continue;
                }
                for (CampeonatoPartida partida : rodada.getPartidas()) {
                    if (partida.getStatus() != StatusPartida.FINALIZADA) {
                        continue;
                    }
                    if (partida.getEventos() != null) {
                        partida.getEventos().size();
                    }
                    aplicarPlacarNaClassificacao(partida, clubesPorId);
                    aplicarEventosNasEstatisticas(partida, atletasPorId);
                }
            }
        }

        campeonatoAtletaRepository.saveAll(atletas);
        campeonatoRepository.save(campeonato);
        campeonatoSuspensaoService.recalcularSuspensoes(campeonato, atletas);
    }

    @Transactional(readOnly = true)
    public CampeonatoEstatisticasDTO obterEstatisticas(Long campeonatoId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new CampeonatoBusinessException("Campeonato não encontrado."));

        List<CampeonatoAtleta> atletas = campeonatoAtletaRepository.findByCampeonatoCampeonatoId(campeonatoId);
        atletas.forEach(CampeonatoAtletaIdentidade::garantir);
        CampeonatoEstatisticasDTO dto = new CampeonatoEstatisticasDTO();

        if (campeonato.getClubes() != null) {
            dto.setClassificacao(campeonato.getClubes().stream()
                    .map(this::toClassificacaoDTO)
                    .sorted(comparadorClassificacao())
                    .collect(Collectors.toList()));
        }

        List<RankingAtletaCampeonatoDTO> rankingsAgregados = agregarRankingsPorIdentidade(atletas);

        dto.setArtilharia(rankingsAgregados.stream()
                .filter(a -> valor(a.getGols()) > 0)
                .sorted(Comparator
                        .comparing((RankingAtletaCampeonatoDTO a) -> valor(a.getGols()), Comparator.reverseOrder())
                        .thenComparing(this::nomeRanking, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList()));

        dto.setAssistencias(rankingsAgregados.stream()
                .filter(a -> valor(a.getAssistencias()) > 0)
                .sorted(Comparator
                        .comparing((RankingAtletaCampeonatoDTO a) -> valor(a.getAssistencias()), Comparator.reverseOrder())
                        .thenComparing(this::nomeRanking, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList()));

        dto.setCartoesAmarelos(rankingsAgregados.stream()
                .filter(a -> valor(a.getCartoesAmarelos()) > 0)
                .sorted(Comparator
                        .comparing((RankingAtletaCampeonatoDTO a) -> valor(a.getCartoesAmarelos()), Comparator.reverseOrder())
                        .thenComparing(this::nomeRanking, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList()));

        dto.setCartoesVermelhos(rankingsAgregados.stream()
                .filter(a -> valor(a.getCartoesVermelhos()) > 0)
                .sorted(Comparator
                        .comparing((RankingAtletaCampeonatoDTO a) -> valor(a.getCartoesVermelhos()), Comparator.reverseOrder())
                        .thenComparing(this::nomeRanking, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList()));

        dto.setDashboard(montarDashboard(campeonato, atletas, dto));
        return dto;
    }

    private CampeonatoDashboardDTO montarDashboard(
            Campeonato campeonato,
            List<CampeonatoAtleta> atletas,
            CampeonatoEstatisticasDTO stats) {

        CampeonatoDashboardDTO dash = new CampeonatoDashboardDTO();

        if (campeonato.getCampeaoClube() != null) {
            dash.setCampeaoClubeId(campeonato.getCampeaoClube().getCampeonatoClubeId());
            dash.setCampeaoNome(campeonato.getCampeaoClube().getNome());
            resolverVice(campeonato).ifPresent(vice -> {
                dash.setViceClubeId(vice.getCampeonatoClubeId());
                dash.setViceNome(vice.getNome());
            });
        }

        if (!stats.getArtilharia().isEmpty()) {
            dash.setArtilheiro(stats.getArtilharia().get(0));
        }
        if (!stats.getAssistencias().isEmpty()) {
            dash.setLiderAssistencias(stats.getAssistencias().get(0));
        }
        if (!stats.getCartoesAmarelos().isEmpty()) {
            dash.setMaisCartoesAmarelos(stats.getCartoesAmarelos().get(0));
        }
        if (!stats.getCartoesVermelhos().isEmpty()) {
            dash.setMaisCartoesVermelhos(stats.getCartoesVermelhos().get(0));
        }

        List<ClassificacaoClubeDTO> clubesComJogos = stats.getClassificacao().stream()
                .filter(c -> valor(c.getJogos()) > 0)
                .collect(Collectors.toList());

        clubesComJogos.stream()
                .max(Comparator.comparingInt(c -> valor(c.getGolsPro())))
                .ifPresent(dash::setMelhorAtaque);
        clubesComJogos.stream()
                .min(Comparator.comparingInt(c -> valor(c.getGolsContra())))
                .ifPresent(dash::setMelhorDefesa);
        clubesComJogos.stream()
                .max(Comparator.comparingInt(c -> valor(c.getSaldoGols())))
                .ifPresent(dash::setMaiorSaldo);

        int partidasFinalizadas = 0;
        int totalGols = 0;
        if (campeonato.getRodadas() != null) {
            for (CampeonatoRodada rodada : campeonato.getRodadas()) {
                if (rodada.getPartidas() == null) {
                    continue;
                }
                for (CampeonatoPartida partida : rodada.getPartidas()) {
                    if (partida.getStatus() != StatusPartida.FINALIZADA) {
                        continue;
                    }
                    partidasFinalizadas++;
                    totalGols += valor(partida.getGolsMandante()) + valor(partida.getGolsVisitante());
                }
            }
        }

        int totalCartoes = atletas.stream()
                .mapToInt(a -> valor(a.getCartoesAmarelos()) + valor(a.getCartoesVermelhos()))
                .sum();

        Set<String> identidadesCriadas = new HashSet<>();
        int transferencias = 0;
        for (CampeonatoAtleta atleta : atletas) {
            if (atleta.getDataFim() != null) {
                transferencias++;
            }
            if (atleta.getAtletaOrigemId() == null) {
                identidadesCriadas.add(CampeonatoAtletaIdentidade.garantir(atleta));
            }
        }

        dash.setQuantidadePartidas(partidasFinalizadas);
        dash.setQuantidadeGols(totalGols);
        dash.setMediaGols(partidasFinalizadas == 0 ? 0.0
                : Math.round((totalGols * 100.0) / partidasFinalizadas) / 100.0);
        dash.setQuantidadeCartoes(totalCartoes);
        dash.setQuantidadeTransferencias(transferencias);
        dash.setQuantidadeAtletasCriados(identidadesCriadas.size());
        return dash;
    }

    private java.util.Optional<CampeonatoClube> resolverVice(Campeonato campeonato) {
        if (campeonato.getCampeaoClube() == null || campeonato.getRodadas() == null) {
            return java.util.Optional.empty();
        }
        Long campeaoId = campeonato.getCampeaoClube().getCampeonatoClubeId();

        CampeonatoPartida finalDoCampeonato = campeonato.getRodadas().stream()
                .filter(r -> r.getNome() != null && r.getNome().equalsIgnoreCase("Final"))
                .flatMap(r -> r.getPartidas() == null ? java.util.stream.Stream.empty() : r.getPartidas().stream())
                .filter(p -> p.getStatus() == StatusPartida.FINALIZADA)
                .findFirst()
                .orElse(null);

        if (finalDoCampeonato == null) {
            finalDoCampeonato = campeonato.getRodadas().stream()
                    .max(Comparator.comparing(r -> r.getNumeroRodada() == null ? 0 : r.getNumeroRodada()))
                    .flatMap(r -> r.getPartidas() == null ? java.util.Optional.empty()
                            : r.getPartidas().stream()
                                    .filter(p -> p.getStatus() == StatusPartida.FINALIZADA)
                                    .filter(p -> envolveClube(p, campeaoId))
                                    .findFirst())
                    .orElse(null);
        }

        if (finalDoCampeonato == null) {
            return java.util.Optional.empty();
        }
        CampeonatoClube mandante = finalDoCampeonato.getClubeMandante();
        CampeonatoClube visitante = finalDoCampeonato.getClubeVisitante();
        if (mandante != null && !Objects.equals(mandante.getCampeonatoClubeId(), campeaoId)) {
            return java.util.Optional.of(mandante);
        }
        if (visitante != null && !Objects.equals(visitante.getCampeonatoClubeId(), campeaoId)) {
            return java.util.Optional.of(visitante);
        }
        return java.util.Optional.empty();
    }

    private boolean envolveClube(CampeonatoPartida partida, Long clubeId) {
        return (partida.getClubeMandante() != null
                && Objects.equals(partida.getClubeMandante().getCampeonatoClubeId(), clubeId))
                || (partida.getClubeVisitante() != null
                        && Objects.equals(partida.getClubeVisitante().getCampeonatoClubeId(), clubeId));
    }

    private Comparator<ClassificacaoClubeDTO> comparadorClassificacao() {
        return Comparator
                .comparing((ClassificacaoClubeDTO c) -> valor(c.getPontos()), Comparator.reverseOrder())
                .thenComparing(c -> valor(c.getSaldoGols()), Comparator.reverseOrder())
                .thenComparing(c -> valor(c.getGolsPro()), Comparator.reverseOrder())
                .thenComparing(c -> valor(c.getVitorias()), Comparator.reverseOrder())
                .thenComparing(c -> valor(c.getDerrotas()))
                .thenComparing(ClassificacaoClubeDTO::getNome, Comparator.nullsLast(String::compareToIgnoreCase));
    }

    private List<RankingAtletaCampeonatoDTO> agregarRankingsPorIdentidade(List<CampeonatoAtleta> atletas) {
        Map<String, List<CampeonatoAtleta>> porIdentidade = new HashMap<>();
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

            RankingAtletaCampeonatoDTO dto = new RankingAtletaCampeonatoDTO();
            dto.setIdentidade(entry.getKey());
            dto.setCampeonatoAtletaId(ativo.getCampeonatoAtletaId());
            dto.setNome(ativo.getNome());
            dto.setSobrenome(ativo.getSobrenome());
            if (ativo.getCampeonatoClube() != null) {
                dto.setCampeonatoClubeId(ativo.getCampeonatoClube().getCampeonatoClubeId());
                dto.setClubeNome(ativo.getCampeonatoClube().getNome());
            }

            int gols = 0;
            int assists = 0;
            int amarelos = 0;
            int vermelhos = 0;
            int golsContra = 0;
            for (CampeonatoAtleta v : vinculos) {
                gols += valor(v.getGols());
                assists += valor(v.getAssistencias());
                amarelos += valor(v.getCartoesAmarelos());
                vermelhos += valor(v.getCartoesVermelhos());
                golsContra += valor(v.getGolsContra());
            }
            dto.setGols(gols);
            dto.setAssistencias(assists);
            dto.setCartoesAmarelos(amarelos);
            dto.setCartoesVermelhos(vermelhos);
            dto.setGolsContra(golsContra);
            resultado.add(dto);
        }
        return resultado;
    }

    private String nomeRanking(RankingAtletaCampeonatoDTO dto) {
        if (dto.getSobrenome() == null || dto.getSobrenome().isBlank()) {
            return dto.getNome() == null ? "" : dto.getNome();
        }
        return dto.getNome() + " " + dto.getSobrenome();
    }

    /**
     * Aplica placar na classificação. Pênaltis não alteram pontos, gols nem saldo.
     * Bônus: diferença de 3–4 gols (+1) ou 5+ (+2).
     */
    private void aplicarPlacarNaClassificacao(CampeonatoPartida partida, Map<Long, CampeonatoClube> clubesPorId) {
        if (partida.getGolsMandante() == null || partida.getGolsVisitante() == null) {
            return;
        }
        CampeonatoClube mandante = clubesPorId.get(partida.getClubeMandante().getCampeonatoClubeId());
        CampeonatoClube visitante = clubesPorId.get(partida.getClubeVisitante().getCampeonatoClubeId());
        if (mandante == null || visitante == null) {
            return;
        }

        int golsM = partida.getGolsMandante();
        int golsV = partida.getGolsVisitante();

        mandante.setJogos(valor(mandante.getJogos()) + 1);
        visitante.setJogos(valor(visitante.getJogos()) + 1);

        mandante.setGolsPro(valor(mandante.getGolsPro()) + golsM);
        mandante.setGolsContra(valor(mandante.getGolsContra()) + golsV);
        visitante.setGolsPro(valor(visitante.getGolsPro()) + golsV);
        visitante.setGolsContra(valor(visitante.getGolsContra()) + golsM);

        if (golsM > golsV) {
            int bonus = calcularBonus(golsM - golsV);
            mandante.setVitorias(valor(mandante.getVitorias()) + 1);
            mandante.setPontos(valor(mandante.getPontos()) + 3 + bonus);
            visitante.setDerrotas(valor(visitante.getDerrotas()) + 1);
        } else if (golsV > golsM) {
            int bonus = calcularBonus(golsV - golsM);
            visitante.setVitorias(valor(visitante.getVitorias()) + 1);
            visitante.setPontos(valor(visitante.getPontos()) + 3 + bonus);
            mandante.setDerrotas(valor(mandante.getDerrotas()) + 1);
        } else {
            mandante.setEmpates(valor(mandante.getEmpates()) + 1);
            visitante.setEmpates(valor(visitante.getEmpates()) + 1);
            mandante.setPontos(valor(mandante.getPontos()) + 1);
            visitante.setPontos(valor(visitante.getPontos()) + 1);
        }
    }

    static int calcularBonus(int diferencaGols) {
        if (diferencaGols >= 5) {
            return 2;
        }
        if (diferencaGols >= 3) {
            return 1;
        }
        return 0;
    }

    private void aplicarEventosNasEstatisticas(CampeonatoPartida partida, Map<Long, CampeonatoAtleta> atletasPorId) {
        if (partida.getEventos() == null) {
            return;
        }
        for (CampeonatoPartidaEvento evento : partida.getEventos()) {
            if (evento.getAtleta() == null) {
                continue;
            }
            CampeonatoAtleta atleta = atletasPorId.get(evento.getAtleta().getCampeonatoAtletaId());
            if (atleta == null) {
                continue;
            }
            TipoEventoPartida tipo = evento.getTipo();
            if (tipo == TipoEventoPartida.GOL) {
                atleta.setGols(valor(atleta.getGols()) + 1);
            } else if (tipo == TipoEventoPartida.GOL_CONTRA) {
                atleta.setGolsContra(valor(atleta.getGolsContra()) + 1);
            } else if (tipo == TipoEventoPartida.ASSISTENCIA) {
                atleta.setAssistencias(valor(atleta.getAssistencias()) + 1);
            } else if (tipo == TipoEventoPartida.CARTAO_AMARELO) {
                atleta.setCartoesAmarelos(valor(atleta.getCartoesAmarelos()) + 1);
            } else if (tipo == TipoEventoPartida.CARTAO_VERMELHO) {
                atleta.setCartoesVermelhos(valor(atleta.getCartoesVermelhos()) + 1);
            }
        }
    }

    private void substituirEventos(
            CampeonatoPartida partida,
            List<PartidaEventoRequestDTO> eventosRequest,
            List<CampeonatoAtleta> atletasResolvidos) {

        if (partida.getEventos() == null) {
            partida.setEventos(new ArrayList<>());
        }
        partida.getEventos().clear();

        for (int i = 0; i < eventosRequest.size(); i++) {
            PartidaEventoRequestDTO req = eventosRequest.get(i);
            CampeonatoPartidaEvento evento = new CampeonatoPartidaEvento();
            evento.setPartida(partida);
            evento.setTipo(req.getTipo());
            evento.setAtleta(atletasResolvidos.get(i));
            evento.setOrdem(i + 1);
            partida.getEventos().add(evento);
        }
    }

    private List<CampeonatoAtleta> resolverAtletas(Long campeonatoId, List<PartidaEventoRequestDTO> eventos) {
        List<CampeonatoAtleta> resolvidos = new ArrayList<>();
        Map<Long, CampeonatoAtleta> cache = new HashMap<>();

        for (PartidaEventoRequestDTO evento : eventos) {
            if (evento.getCampeonatoAtletaId() == null) {
                resolvidos.add(null);
                continue;
            }
            CampeonatoAtleta atleta = cache.computeIfAbsent(evento.getCampeonatoAtletaId(), id ->
                    campeonatoAtletaRepository.findById(id).orElse(null));
            if (atleta == null
                    || atleta.getCampeonato() == null
                    || !Objects.equals(atleta.getCampeonato().getCampeonatoId(), campeonatoId)) {
                throw new CampeonatoBusinessException(
                        "Atleta informado não pertence a este campeonato: " + evento.getCampeonatoAtletaId());
            }
            resolvidos.add(atleta);
        }
        return resolvidos;
    }

    private CampeonatoPartida obterPartidaDoCampeonato(Long campeonatoId, Long partidaId) {
        CampeonatoPartida partida = campeonatoPartidaRepository
                .findByCampeonatoPartidaIdAndCampeonatoRodadaCampeonatoCampeonatoId(partidaId, campeonatoId)
                .orElseThrow(() -> new CampeonatoBusinessException("Partida não encontrada neste campeonato."));

        if (partida.getEventos() != null) {
            partida.getEventos().size();
        }
        return partida;
    }

    private PartidaDetalheResponseDTO toDetalheDTO(Long campeonatoId, CampeonatoPartida partida) {
        PartidaDetalheResponseDTO dto = new PartidaDetalheResponseDTO();
        dto.setCampeonatoPartidaId(partida.getCampeonatoPartidaId());
        dto.setCampeonatoId(campeonatoId);
        dto.setOrdem(partida.getOrdem());
        dto.setStatus(partida.getStatus());
        dto.setGolsMandante(partida.getGolsMandante());
        dto.setGolsVisitante(partida.getGolsVisitante());
        dto.setDisputouPenaltis(partida.getDisputouPenaltis());
        dto.setPenaltisMandante(partida.getPenaltisMandante());
        dto.setPenaltisVisitante(partida.getPenaltisVisitante());

        CampeonatoClube mandante = partida.getClubeMandante();
        CampeonatoClube visitante = partida.getClubeVisitante();

        if (mandante != null) {
            dto.setClubeMandanteId(mandante.getCampeonatoClubeId());
            dto.setClubeMandanteNome(mandante.getNome());
            dto.setClubeMandanteSigla(mandante.getSigla());
            dto.setClubeMandanteRank(mandante.getRank());
            dto.setClubeMandanteCompetidor(mandante.getCompetidorNumero());
        }
        if (visitante != null) {
            dto.setClubeVisitanteId(visitante.getCampeonatoClubeId());
            dto.setClubeVisitanteNome(visitante.getNome());
            dto.setClubeVisitanteSigla(visitante.getSigla());
            dto.setClubeVisitanteRank(visitante.getRank());
            dto.setClubeVisitanteCompetidor(visitante.getCompetidorNumero());
        }
        if (partida.getClubeVencedor() != null) {
            dto.setClubeVencedorId(partida.getClubeVencedor().getCampeonatoClubeId());
            dto.setClubeVencedorNome(partida.getClubeVencedor().getNome());
        }

        List<Long> clubeIds = new ArrayList<>();
        if (mandante != null) {
            clubeIds.add(mandante.getCampeonatoClubeId());
        }
        if (visitante != null) {
            clubeIds.add(visitante.getCampeonatoClubeId());
        }

        List<CampeonatoAtleta> atletas = clubeIds.isEmpty()
                ? List.of()
                : campeonatoAtletaRepository.findByCampeonatoClubeCampeonatoClubeIdInAndAtivoTrue(clubeIds);

        Set<String> suspensos = partida.getStatus() == StatusPartida.FINALIZADA
                ? Set.of()
                : campeonatoSuspensaoService.identidadesSuspensasAtivas(campeonatoId);

        dto.setAtletasMandante(atletas.stream()
                .filter(a -> mandante != null
                        && Objects.equals(a.getCampeonatoClube().getCampeonatoClubeId(), mandante.getCampeonatoClubeId()))
                .sorted(Comparator.comparing(this::nomeCompleto, String.CASE_INSENSITIVE_ORDER))
                .map(a -> toAtletaPartidaDTO(a, suspensos))
                .collect(Collectors.toList()));

        dto.setAtletasVisitante(atletas.stream()
                .filter(a -> visitante != null
                        && Objects.equals(a.getCampeonatoClube().getCampeonatoClubeId(), visitante.getCampeonatoClubeId()))
                .sorted(Comparator.comparing(this::nomeCompleto, String.CASE_INSENSITIVE_ORDER))
                .map(a -> toAtletaPartidaDTO(a, suspensos))
                .collect(Collectors.toList()));

        if (partida.getEventos() != null) {
            dto.setEventos(partida.getEventos().stream()
                    .sorted(Comparator.comparing(e -> e.getOrdem() == null ? 0 : e.getOrdem()))
                    .map(this::toEventoDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private CampeonatoAtletaPartidaDTO toAtletaPartidaDTO(CampeonatoAtleta atleta, Set<String> suspensos) {
        CampeonatoAtletaPartidaDTO dto = new CampeonatoAtletaPartidaDTO();
        dto.setCampeonatoAtletaId(atleta.getCampeonatoAtletaId());
        dto.setCampeonatoClubeId(atleta.getCampeonatoClube().getCampeonatoClubeId());
        dto.setNome(atleta.getNome());
        dto.setSobrenome(atleta.getSobrenome());
        dto.setPosicao(atleta.getPosicao());
        String identidade = CampeonatoAtletaIdentidade.garantir(atleta);
        boolean suspenso = suspensos.contains(identidade);
        dto.setSuspenso(suspenso);
        if (suspenso) {
            dto.setMotivoSuspensao(CampeonatoSuspensaoService.MOTIVO_EXPULSAO);
        }
        return dto;
    }

    private PartidaEventoResponseDTO toEventoDTO(CampeonatoPartidaEvento evento) {
        PartidaEventoResponseDTO dto = new PartidaEventoResponseDTO();
        dto.setCampeonatoPartidaEventoId(evento.getCampeonatoPartidaEventoId());
        dto.setTipo(evento.getTipo());
        dto.setOrdem(evento.getOrdem());
        if (evento.getAtleta() != null) {
            dto.setCampeonatoAtletaId(evento.getAtleta().getCampeonatoAtletaId());
            dto.setAtletaNome(evento.getAtleta().getNome());
            dto.setAtletaSobrenome(evento.getAtleta().getSobrenome());
            if (evento.getAtleta().getCampeonatoClube() != null) {
                dto.setCampeonatoClubeId(evento.getAtleta().getCampeonatoClube().getCampeonatoClubeId());
                dto.setClubeNome(evento.getAtleta().getCampeonatoClube().getNome());
            }
        }
        return dto;
    }

    private ClassificacaoClubeDTO toClassificacaoDTO(CampeonatoClube clube) {
        ClassificacaoClubeDTO dto = new ClassificacaoClubeDTO();
        dto.setCampeonatoClubeId(clube.getCampeonatoClubeId());
        dto.setClubeOrigemId(clube.getClubeOrigemId());
        dto.setNome(clube.getNome());
        dto.setSigla(clube.getSigla());
        dto.setCompetidorNumero(clube.getCompetidorNumero());
        dto.setEliminado(clube.getEliminado());
        dto.setJogos(valor(clube.getJogos()));
        dto.setPontos(valor(clube.getPontos()));
        dto.setVitorias(valor(clube.getVitorias()));
        dto.setEmpates(valor(clube.getEmpates()));
        dto.setDerrotas(valor(clube.getDerrotas()));
        dto.setGolsPro(valor(clube.getGolsPro()));
        dto.setGolsContra(valor(clube.getGolsContra()));
        dto.setSaldoGols(clube.getSaldoGols());
        return dto;
    }

    private String nomeCompleto(CampeonatoAtleta atleta) {
        if (atleta.getSobrenome() == null || atleta.getSobrenome().isBlank()) {
            return atleta.getNome() == null ? "" : atleta.getNome();
        }
        return atleta.getNome() + " " + atleta.getSobrenome();
    }

    private int valor(Integer value) {
        return value == null ? 0 : value;
    }
}
