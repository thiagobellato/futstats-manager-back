package br.com.bellato.gerenciador_fifa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoCriarRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoValidacaoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClubeSelecionadoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClubesPorRankResponseDTO;
import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.mapper.campeonato.CampeonatoMapper;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoDistribuicaoRank;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoAtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.validator.CampeonatoValidator;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CampeonatoService {

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private CampeonatoAtletaRepository campeonatoAtletaRepository;

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private ClubeRankService clubeRankService;

    @Autowired
    private AtletaRepository atletaRepository;

    public List<Campeonato> obterTodos() {
        return campeonatoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Campeonato obterPorId(Long id) {
        return campeonatoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public CampeonatoResponseCompletoDTO obterCompletoPorId(Long id) {
        Campeonato campeonato = obterPorId(id);
        List<br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta> atletas =
                campeonatoAtletaRepository.findByCampeonatoCampeonatoId(id);
        Map<Long, Long> atletasPorClube = new HashMap<>();
        for (br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta atleta : atletas) {
            if (atleta.getCampeonatoClube() != null) {
                Long clubeId = atleta.getCampeonatoClube().getCampeonatoClubeId();
                atletasPorClube.merge(clubeId, 1L, Long::sum);
            }
        }
        return CampeonatoMapper.toDTOCompleto(campeonato, atletas.size(), atletasPorClube);
    }

    public ClubesPorRankResponseDTO obterClubesPorRank() {
        List<Clube> clubes = clubeRepository.findAll();

        Map<ClubRank, List<br.com.bellato.gerenciador_fifa.dto.campeonato.ClubeDisponivelDTO>> agrupado =
                new EnumMap<>(ClubRank.class);
        for (ClubRank rank : ClubRank.values()) {
            agrupado.put(rank, new ArrayList<>());
        }

        for (Clube clube : clubes) {
            if (clube.getRank() == null) {
                continue;
            }
            agrupado.get(clube.getRank()).add(CampeonatoMapper.toClubeDisponivel(clube));
        }

        ClubesPorRankResponseDTO response = new ClubesPorRankResponseDTO();
        response.setClubesPorRank(agrupado);
        return response;
    }

    public CampeonatoValidacaoResponseDTO validar(CampeonatoCriarRequestDTO dto) {
        CampeonatoValidacaoResponseDTO response = new CampeonatoValidacaoResponseDTO();
        try {
            Map<Long, Clube> clubesPorId = obterClubesPorIdMap();
            Map<ClubRank, Long> disponibilidadePorRank = clubeRankService.obterQuantidadePorRank();
            CampeonatoValidator.validarCriacao(dto, clubesPorId, disponibilidadePorRank);
            response.setValido(true);
            response.setMensagem("Dados válidos para criação do campeonato.");
            response.setQuantidadeAtletasEstimada(estimarQuantidadeAtletas(dto));
        } catch (CampeonatoBusinessException ex) {
            response.setValido(false);
            response.setMensagem(ex.getMessage());
        }
        return response;
    }

    @Transactional
    public CampeonatoResponseDTO criar(CampeonatoCriarRequestDTO dto) {
        Map<Long, Clube> clubesPorId = obterClubesPorIdMap();
        Map<ClubRank, Long> disponibilidadePorRank = clubeRankService.obterQuantidadePorRank();
        CampeonatoValidator.validarCriacao(dto, clubesPorId, disponibilidadePorRank);

        Campeonato campeonato = new Campeonato();
        campeonato.setNome(dto.getNome().trim());
        campeonato.setQuantidadeClubes(dto.getQuantidadeClubes());
        campeonato.setStatus(StatusCampeonato.AGUARDANDO_INICIO);
        campeonato.setCompetidor1Nome(dto.getCompetidor1Nome().trim());
        campeonato.setCompetidor2Nome(dto.getCompetidor2Nome().trim());
        campeonato.setPossuiCampeaoAnterior(Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior()));
        campeonato.setDataCriacao(LocalDateTime.now());

        if (Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior())) {
            campeonato.setCampeaoAnteriorCompetidor(dto.getCampeaoAnteriorCompetidor());
            campeonato.setCampeaoAnteriorClubeOrigemId(dto.getCampeaoAnteriorClubeId());
        }

        for (ClubRank rank : ClubRank.values()) {
            CampeonatoDistribuicaoRank distribuicao = new CampeonatoDistribuicaoRank();
            distribuicao.setCampeonato(campeonato);
            distribuicao.setRank(rank);
            distribuicao.setQuantidade(dto.getDistribuicaoRanks().get(rank));
            campeonato.getDistribuicaoRanks().add(distribuicao);
        }

        Map<Long, CampeonatoClube> snapshotsPorClubeOrigem = new HashMap<>();

        for (ClubeSelecionadoDTO selecionado : dto.getClubesSelecionados()) {
            Clube clubeOrigem = clubesPorId.get(selecionado.getClubeId());
            CampeonatoClube snapshot = criarSnapshotClube(campeonato, clubeOrigem, selecionado.getRank(), dto);
            campeonato.getClubes().add(snapshot);
            snapshotsPorClubeOrigem.put(selecionado.getClubeId(), snapshot);
        }

        for (ClubeSelecionadoDTO selecionado : dto.getClubesSelecionados()) {
            CampeonatoClube snapshotClube = snapshotsPorClubeOrigem.get(selecionado.getClubeId());
            List<Atleta> atletas = atletaRepository.findByClubeClubeId(selecionado.getClubeId());
            for (Atleta atleta : atletas) {
                campeonato.getAtletas().add(criarSnapshotAtleta(campeonato, snapshotClube, atleta));
            }
        }

        Campeonato salvo = campeonatoRepository.save(campeonato);
        return CampeonatoMapper.toDTO(salvo);
    }

    private CampeonatoClube criarSnapshotClube(Campeonato campeonato, Clube clubeOrigem, ClubRank rank,
            CampeonatoCriarRequestDTO dto) {
        CampeonatoClube snapshot = new CampeonatoClube();
        snapshot.setCampeonato(campeonato);
        snapshot.setClubeOrigemId(clubeOrigem.getClubeId());
        snapshot.setNome(clubeOrigem.getNome());
        snapshot.setSigla(clubeOrigem.getSigla());
        snapshot.setPais(clubeOrigem.getPais());
        snapshot.setRank(rank);

        boolean isCampeao = Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior())
                && Long.valueOf(clubeOrigem.getClubeId()).equals(dto.getCampeaoAnteriorClubeId());

        snapshot.setCampeaoAnterior(isCampeao);
        snapshot.setExcluidoSorteio(isCampeao);

        if (isCampeao) {
            snapshot.setCompetidorNumero(dto.getCampeaoAnteriorCompetidor());
        }

        return snapshot;
    }

    private CampeonatoAtleta criarSnapshotAtleta(Campeonato campeonato, CampeonatoClube snapshotClube, Atleta atleta) {
        CampeonatoAtleta snapshot = new CampeonatoAtleta();
        snapshot.setCampeonato(campeonato);
        snapshot.setCampeonatoClube(snapshotClube);
        snapshot.setAtletaOrigemId(atleta.getAtletaId());
        snapshot.setNome(atleta.getNome());
        snapshot.setSobrenome(atleta.getSobrenome());
        snapshot.setDataDeNascimento(atleta.getDataDeNascimento());
        snapshot.setNacionalidade(atleta.getNacionalidade());
        snapshot.setPosicao(atleta.getPosicao());
        return snapshot;
    }

    private Map<Long, Clube> obterClubesPorIdMap() {
        return clubeRepository.findAll().stream()
                .collect(Collectors.toMap(Clube::getClubeId, Function.identity()));
    }

    private long estimarQuantidadeAtletas(CampeonatoCriarRequestDTO dto) {
        if (dto.getClubesSelecionados() == null) {
            return 0;
        }
        long total = 0;
        for (ClubeSelecionadoDTO selecionado : dto.getClubesSelecionados()) {
            total += atletaRepository.findByClubeClubeId(selecionado.getClubeId()).size();
        }
        return total;
    }
}
