package br.com.bellato.gerenciador_fifa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoComposicaoResponseDTO;
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
import br.com.bellato.gerenciador_fifa.model.CampeonatoParticipante;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.User;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoAtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoParticipanteRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.UserRepository;
import br.com.bellato.gerenciador_fifa.service.transferencia.CampeonatoAtletaIdentidade;
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

    @Autowired
    private CampeonatoMotorService campeonatoMotorService;

    @Autowired
    private CampeonatoPartidaService campeonatoPartidaService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampeonatoParticipanteRepository campeonatoParticipanteRepository;

    @Autowired
    private UserService userService;

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
        // Carrega associações em queries separadas (evita MultipleBagFetchException).
        Campeonato campeonato = campeonatoRepository.findByIdComClubesECampeao(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com o ID: " + id));
        campeonatoRepository.findByIdComDistribuicao(id);
        campeonatoRepository.findByIdComRodadas(id);

        if (campeonato.getRodadas() != null) {
            campeonato.getRodadas().forEach(rodada -> {
                if (rodada.getPartidas() != null) {
                    rodada.getPartidas().size();
                }
            });
        }

        List<CampeonatoAtleta> atletas = campeonatoAtletaRepository.findByCampeonatoCampeonatoId(id);
        Map<Long, Long> atletasPorClube = new HashMap<>();
        long atletasAtivos = 0;
        for (CampeonatoAtleta atleta : atletas) {
            if (!atleta.isAtivo()) {
                continue;
            }
            atletasAtivos++;
            if (atleta.getCampeonatoClube() != null) {
                Long clubeId = atleta.getCampeonatoClube().getCampeonatoClubeId();
                atletasPorClube.merge(clubeId, 1L, Long::sum);
            }
        }
        CampeonatoResponseCompletoDTO dto = CampeonatoMapper.toDTOCompleto(campeonato, atletasAtivos, atletasPorClube);
        dto.setEstatisticas(campeonatoPartidaService.obterEstatisticas(id));
        return dto;
    }

    public ClubesPorRankResponseDTO obterClubesPorRank() {
        return obterClubesPorRank(null);
    }

    public ClubesPorRankResponseDTO obterClubesPorRank(Long campeaoAnteriorClubeId) {
        List<Clube> clubes = clubeRepository.findAllComEstatistica();

        Map<ClubRank, List<br.com.bellato.gerenciador_fifa.dto.campeonato.ClubeDisponivelDTO>> agrupado =
                new EnumMap<>(ClubRank.class);
        for (ClubRank rank : ClubRank.values()) {
            agrupado.put(rank, new ArrayList<>());
        }

        for (Clube clube : clubes) {
            if (clube.getRank() == null) {
                continue;
            }
            boolean protegido = campeaoAnteriorClubeId != null
                    && Long.valueOf(clube.getClubeId()).equals(campeaoAnteriorClubeId);
            agrupado.get(clube.getRank()).add(CampeonatoMapper.toClubeDisponivel(clube, protegido));
        }

        ClubesPorRankResponseDTO response = new ClubesPorRankResponseDTO();
        response.setClubesPorRank(agrupado);
        return response;
    }

    public CampeonatoComposicaoResponseDTO obterComposicao(Integer quantidadeClubes, Boolean possuiCampeaoAnterior,
            Long campeaoAnteriorClubeId) {
        if (quantidadeClubes == null || !Set.of(16, 32, 64).contains(quantidadeClubes)) {
            throw new CampeonatoBusinessException("A quantidade de clubes deve ser 16, 32 ou 64.");
        }

        boolean protegido = Boolean.TRUE.equals(possuiCampeaoAnterior);
        int vagasProtegido = CampeonatoValidator.calcularVagasCampeaoProtegido(protegido);
        int vagasSelecao = CampeonatoValidator.calcularVagasParaSelecao(quantidadeClubes, protegido);

        Map<ClubRank, Long> quantidadePorRank = new EnumMap<>(ClubRank.class);
        quantidadePorRank.putAll(clubeRankService.obterQuantidadePorRank());

        if (protegido && campeaoAnteriorClubeId != null) {
            clubeRepository.findByIdComEstatistica(campeaoAnteriorClubeId).ifPresent(campeao -> {
                if (campeao.getRank() != null) {
                    long atual = quantidadePorRank.getOrDefault(campeao.getRank(), 0L);
                    quantidadePorRank.put(campeao.getRank(), Math.max(0L, atual - 1));
                }
            });
        }

        long totalDisponivel = quantidadePorRank.values().stream().mapToLong(Long::longValue).sum();

        CampeonatoComposicaoResponseDTO response = new CampeonatoComposicaoResponseDTO();
        response.setQuantidadeParticipantes(quantidadeClubes);
        response.setPossuiCampeaoProtegido(protegido);
        response.setVagasCampeaoProtegido(vagasProtegido);
        response.setVagasParaSelecao(vagasSelecao);
        response.setQuantidadePorRankParaSelecao(quantidadePorRank);
        response.setTotalClubesDisponiveisParaSelecao(totalDisponivel);
        return response;
    }

    public CampeonatoValidacaoResponseDTO validar(CampeonatoCriarRequestDTO dto) {
        CampeonatoValidacaoResponseDTO response = new CampeonatoValidacaoResponseDTO();
        try {
            aplicarParticipante1Autenticado(dto);
            Map<Long, Clube> clubesPorId = obterClubesPorIdMap();
            Map<ClubRank, Long> disponibilidadePorRank = clubeRankService.obterQuantidadePorRank();
            CampeonatoValidator.validarCriacao(dto, clubesPorId, disponibilidadePorRank);
            validarUsuariosAutenticados(dto);
            response.setValido(true);
            response.setMensagem("Dados válidos para criação do campeonato.");
            response.setQuantidadeAtletasEstimada(estimarQuantidadeAtletas(dto));
        } catch (CampeonatoBusinessException ex) {
            response.setValido(false);
            response.setMensagem(ex.getMessage());
        } catch (IllegalStateException | EntityNotFoundException ex) {
            response.setValido(false);
            response.setMensagem(ex.getMessage());
        }
        return response;
    }

    @Transactional
    public CampeonatoResponseDTO criar(CampeonatoCriarRequestDTO dto) {
        aplicarParticipante1Autenticado(dto);
        Map<Long, Clube> clubesPorId = obterClubesPorIdMap();
        Map<ClubRank, Long> disponibilidadePorRank = clubeRankService.obterQuantidadePorRank();
        CampeonatoValidator.validarCriacao(dto, clubesPorId, disponibilidadePorRank);
        validarUsuariosAutenticados(dto);

        boolean autenticado = Boolean.TRUE.equals(dto.getAutenticado());
        String nome1;
        String nome2;
        User user1 = null;
        User user2 = null;

        if (autenticado) {
            user1 = userRepository.findById(dto.getCompetidor1UserId())
                    .orElseThrow(() -> new CampeonatoBusinessException("Usuário autenticado não encontrado."));
            user2 = userRepository.findById(dto.getCompetidor2UserId())
                    .orElseThrow(() -> new CampeonatoBusinessException("Usuário do adversário não encontrado."));
            if (!user1.isEnabled()) {
                throw new CampeonatoBusinessException("O usuário autenticado está desabilitado.");
            }
            if (!user2.isEnabled()) {
                throw new CampeonatoBusinessException("O adversário selecionado está desabilitado.");
            }
            nome1 = user1.getUsername();
            nome2 = user2.getUsername();
        } else {
            nome1 = dto.getCompetidor1Nome().trim();
            nome2 = dto.getCompetidor2Nome().trim();
        }

        Campeonato campeonato = new Campeonato();
        campeonato.setNome(dto.getNome().trim());
        campeonato.setQuantidadeClubes(dto.getQuantidadeClubes());
        campeonato.setStatus(StatusCampeonato.AGUARDANDO_INICIO);
        campeonato.setCompetidor1Nome(nome1);
        campeonato.setCompetidor2Nome(nome2);
        campeonato.setAutenticado(autenticado);
        campeonato.setPossuiCampeaoAnterior(Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior()));
        campeonato.setDataCriacao(LocalDateTime.now());

        if (Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior())) {
            campeonato.setCampeaoAnteriorCompetidor(dto.getCampeaoAnteriorCompetidor());
            campeonato.setCampeaoAnteriorClubeOrigemId(dto.getCampeaoAnteriorClubeId());
        }

        campeonato.getParticipantes().add(criarParticipante(campeonato, 1, nome1, autenticado, user1));
        campeonato.getParticipantes().add(criarParticipante(campeonato, 2, nome2, autenticado, user2));

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
        campeonatoMotorService.iniciarCampeonato(salvo);
        return CampeonatoMapper.toDTO(salvo);
    }

    /**
     * Em campeonato autenticado, o Participante 1 é sempre o usuário logado (JWT).
     * Ignora/sobrescreve qualquer competidor1UserId enviado pelo cliente.
     */
    private void aplicarParticipante1Autenticado(CampeonatoCriarRequestDTO dto) {
        if (!Boolean.TRUE.equals(dto.getAutenticado())) {
            return;
        }
        User atual = userService.obterEntidadeAtual();
        dto.setCompetidor1UserId(atual.getUserId());
        dto.setCompetidor1Nome(atual.getUsername());
    }

    private void validarUsuariosAutenticados(CampeonatoCriarRequestDTO dto) {
        if (!Boolean.TRUE.equals(dto.getAutenticado())) {
            return;
        }
        if (dto.getCompetidor2UserId() == null) {
            throw new CampeonatoBusinessException("Selecione o adversário (Participante 2).");
        }
        if (dto.getCompetidor1UserId().equals(dto.getCompetidor2UserId())) {
            throw new CampeonatoBusinessException(
                    "Não é permitido selecionar a si mesmo como adversário.");
        }
        User adversario = userRepository.findById(dto.getCompetidor2UserId())
                .orElseThrow(() -> new CampeonatoBusinessException("Adversário não encontrado."));
        if (!adversario.isEnabled()) {
            throw new CampeonatoBusinessException("O adversário selecionado está desabilitado.");
        }
    }

    private CampeonatoParticipante criarParticipante(Campeonato campeonato, int side, String displayName,
            boolean autenticado, User user) {
        CampeonatoParticipante participante = new CampeonatoParticipante();
        participante.setCampeonato(campeonato);
        participante.setSide(side);
        participante.setDisplayName(displayName);
        participante.setAuthenticated(autenticado);
        participante.setUser(user);
        return participante;
    }

    /**
     * Backfill idempotente: campeonatos legados sem participantes recebem 2 livres
     * a partir de competidor1Nome/competidor2Nome.
     */
    @Transactional
    public int backfillParticipantesLegados() {
        List<Campeonato> todos = campeonatoRepository.findAll();
        int criados = 0;
        for (Campeonato campeonato : todos) {
            long existentes = campeonatoParticipanteRepository.countByCampeonatoCampeonatoId(campeonato.getCampeonatoId());
            if (existentes > 0) {
                continue;
            }
            if (campeonato.getAutenticado() == null) {
                campeonato.setAutenticado(Boolean.FALSE);
            }
            String nome1 = campeonato.getCompetidor1Nome() != null && !campeonato.getCompetidor1Nome().isBlank()
                    ? campeonato.getCompetidor1Nome()
                    : "Competidor 1";
            String nome2 = campeonato.getCompetidor2Nome() != null && !campeonato.getCompetidor2Nome().isBlank()
                    ? campeonato.getCompetidor2Nome()
                    : "Competidor 2";
            campeonatoParticipanteRepository.save(criarParticipante(campeonato, 1, nome1, false, null));
            campeonatoParticipanteRepository.save(criarParticipante(campeonato, 2, nome2, false, null));
            campeonatoRepository.save(campeonato);
            criados += 2;
        }
        return criados;
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
        snapshot.setIdentidade(CampeonatoAtletaIdentidade.paraAtletaGlobal(atleta.getAtletaId()));
        snapshot.setNome(atleta.getNome());
        snapshot.setSobrenome(atleta.getSobrenome());
        snapshot.setDataDeNascimento(atleta.getDataDeNascimento());
        snapshot.setNacionalidade(atleta.getNacionalidade());
        snapshot.setPosicao(atleta.getPosicao());
        snapshot.setAtivo(true);
        snapshot.setDataInicio(java.time.LocalDate.now());
        return snapshot;
    }

    private Map<Long, Clube> obterClubesPorIdMap() {
        return clubeRepository.findAllComEstatistica().stream()
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
