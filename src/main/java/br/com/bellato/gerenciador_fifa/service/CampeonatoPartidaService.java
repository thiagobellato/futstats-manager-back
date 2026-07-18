package br.com.bellato.gerenciador_fifa.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoAtletaPartidaDTO;
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
        }

        Map<Long, CampeonatoAtleta> atletasPorId = atletas.stream()
                .collect(Collectors.toMap(CampeonatoAtleta::getCampeonatoAtletaId, a -> a));

        Map<Long, CampeonatoClube> clubesPorId = new HashMap<>();
        if (campeonato.getClubes() != null) {
            for (CampeonatoClube clube : campeonato.getClubes()) {
                clube.setJogos(0);
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
    }

    @Transactional(readOnly = true)
    public CampeonatoEstatisticasDTO obterEstatisticas(Long campeonatoId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new CampeonatoBusinessException("Campeonato não encontrado."));

        List<CampeonatoAtleta> atletas = campeonatoAtletaRepository.findByCampeonatoCampeonatoId(campeonatoId);
        CampeonatoEstatisticasDTO dto = new CampeonatoEstatisticasDTO();

        if (campeonato.getClubes() != null) {
            dto.setClassificacao(campeonato.getClubes().stream()
                    .map(this::toClassificacaoDTO)
                    .sorted(Comparator
                            .comparing((ClassificacaoClubeDTO c) -> Boolean.TRUE.equals(c.getEliminado()))
                            .thenComparing(ClassificacaoClubeDTO::getSaldoGols, Comparator.reverseOrder())
                            .thenComparing(ClassificacaoClubeDTO::getGolsPro, Comparator.reverseOrder())
                            .thenComparing(ClassificacaoClubeDTO::getNome, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList()));
        }

        dto.setArtilharia(atletas.stream()
                .filter(a -> valor(a.getGols()) > 0)
                .sorted(Comparator
                        .comparing((CampeonatoAtleta a) -> valor(a.getGols()), Comparator.reverseOrder())
                        .thenComparing(a -> nomeCompleto(a), String.CASE_INSENSITIVE_ORDER))
                .map(this::toRankingDTO)
                .collect(Collectors.toList()));

        dto.setAssistencias(atletas.stream()
                .filter(a -> valor(a.getAssistencias()) > 0)
                .sorted(Comparator
                        .comparing((CampeonatoAtleta a) -> valor(a.getAssistencias()), Comparator.reverseOrder())
                        .thenComparing(a -> nomeCompleto(a), String.CASE_INSENSITIVE_ORDER))
                .map(this::toRankingDTO)
                .collect(Collectors.toList()));

        dto.setCartoesAmarelos(atletas.stream()
                .filter(a -> valor(a.getCartoesAmarelos()) > 0)
                .sorted(Comparator
                        .comparing((CampeonatoAtleta a) -> valor(a.getCartoesAmarelos()), Comparator.reverseOrder())
                        .thenComparing(a -> nomeCompleto(a), String.CASE_INSENSITIVE_ORDER))
                .map(this::toRankingDTO)
                .collect(Collectors.toList()));

        dto.setCartoesVermelhos(atletas.stream()
                .filter(a -> valor(a.getCartoesVermelhos()) > 0)
                .sorted(Comparator
                        .comparing((CampeonatoAtleta a) -> valor(a.getCartoesVermelhos()), Comparator.reverseOrder())
                        .thenComparing(a -> nomeCompleto(a), String.CASE_INSENSITIVE_ORDER))
                .map(this::toRankingDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private void aplicarPlacarNaClassificacao(CampeonatoPartida partida, Map<Long, CampeonatoClube> clubesPorId) {
        if (partida.getGolsMandante() == null || partida.getGolsVisitante() == null) {
            return;
        }
        CampeonatoClube mandante = clubesPorId.get(partida.getClubeMandante().getCampeonatoClubeId());
        CampeonatoClube visitante = clubesPorId.get(partida.getClubeVisitante().getCampeonatoClubeId());
        if (mandante == null || visitante == null) {
            return;
        }

        mandante.setJogos(valor(mandante.getJogos()) + 1);
        visitante.setJogos(valor(visitante.getJogos()) + 1);

        mandante.setGolsPro(valor(mandante.getGolsPro()) + partida.getGolsMandante());
        mandante.setGolsContra(valor(mandante.getGolsContra()) + partida.getGolsVisitante());
        visitante.setGolsPro(valor(visitante.getGolsPro()) + partida.getGolsVisitante());
        visitante.setGolsContra(valor(visitante.getGolsContra()) + partida.getGolsMandante());
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
            } else if (tipo == TipoEventoPartida.ASSISTENCIA) {
                atleta.setAssistencias(valor(atleta.getAssistencias()) + 1);
            } else if (tipo == TipoEventoPartida.CARTAO_AMARELO) {
                atleta.setCartoesAmarelos(valor(atleta.getCartoesAmarelos()) + 1);
            } else if (tipo == TipoEventoPartida.CARTAO_VERMELHO) {
                atleta.setCartoesVermelhos(valor(atleta.getCartoesVermelhos()) + 1);
            }
            // GOL_CONTRA não entra na artilharia
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
                : campeonatoAtletaRepository.findByCampeonatoClubeCampeonatoClubeIdIn(clubeIds);

        dto.setAtletasMandante(atletas.stream()
                .filter(a -> mandante != null
                        && Objects.equals(a.getCampeonatoClube().getCampeonatoClubeId(), mandante.getCampeonatoClubeId()))
                .sorted(Comparator.comparing(this::nomeCompleto, String.CASE_INSENSITIVE_ORDER))
                .map(this::toAtletaPartidaDTO)
                .collect(Collectors.toList()));

        dto.setAtletasVisitante(atletas.stream()
                .filter(a -> visitante != null
                        && Objects.equals(a.getCampeonatoClube().getCampeonatoClubeId(), visitante.getCampeonatoClubeId()))
                .sorted(Comparator.comparing(this::nomeCompleto, String.CASE_INSENSITIVE_ORDER))
                .map(this::toAtletaPartidaDTO)
                .collect(Collectors.toList()));

        if (partida.getEventos() != null) {
            dto.setEventos(partida.getEventos().stream()
                    .sorted(Comparator.comparing(e -> e.getOrdem() == null ? 0 : e.getOrdem()))
                    .map(this::toEventoDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private CampeonatoAtletaPartidaDTO toAtletaPartidaDTO(CampeonatoAtleta atleta) {
        CampeonatoAtletaPartidaDTO dto = new CampeonatoAtletaPartidaDTO();
        dto.setCampeonatoAtletaId(atleta.getCampeonatoAtletaId());
        dto.setCampeonatoClubeId(atleta.getCampeonatoClube().getCampeonatoClubeId());
        dto.setNome(atleta.getNome());
        dto.setSobrenome(atleta.getSobrenome());
        dto.setPosicao(atleta.getPosicao());
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
        dto.setNome(clube.getNome());
        dto.setSigla(clube.getSigla());
        dto.setCompetidorNumero(clube.getCompetidorNumero());
        dto.setEliminado(clube.getEliminado());
        dto.setJogos(valor(clube.getJogos()));
        dto.setGolsPro(valor(clube.getGolsPro()));
        dto.setGolsContra(valor(clube.getGolsContra()));
        dto.setSaldoGols(clube.getSaldoGols());
        return dto;
    }

    private RankingAtletaCampeonatoDTO toRankingDTO(CampeonatoAtleta atleta) {
        RankingAtletaCampeonatoDTO dto = new RankingAtletaCampeonatoDTO();
        dto.setCampeonatoAtletaId(atleta.getCampeonatoAtletaId());
        dto.setNome(atleta.getNome());
        dto.setSobrenome(atleta.getSobrenome());
        dto.setGols(valor(atleta.getGols()));
        dto.setAssistencias(valor(atleta.getAssistencias()));
        dto.setCartoesAmarelos(valor(atleta.getCartoesAmarelos()));
        dto.setCartoesVermelhos(valor(atleta.getCartoesVermelhos()));
        if (atleta.getCampeonatoClube() != null) {
            dto.setCampeonatoClubeId(atleta.getCampeonatoClube().getCampeonatoClubeId());
            dto.setClubeNome(atleta.getCampeonatoClube().getNome());
        }
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
