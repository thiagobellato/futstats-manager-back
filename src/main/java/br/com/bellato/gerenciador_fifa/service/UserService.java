package br.com.bellato.gerenciador_fifa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.auth.UserResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.user.PageResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.user.RivalidadeDetalheDTO;
import br.com.bellato.gerenciador_fifa.dto.user.RivalidadeDetalheDTO.ConfrontoTimelineDTO;
import br.com.bellato.gerenciador_fifa.dto.user.RivalidadeItemDTO;
import br.com.bellato.gerenciador_fifa.dto.user.UserSearchItemDTO;
import br.com.bellato.gerenciador_fifa.dto.user.UsuarioPerfilResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.auth.UserMapper;
import br.com.bellato.gerenciador_fifa.model.EstatisticaUsuario;
import br.com.bellato.gerenciador_fifa.model.HistoricoConfrontoUsuario;
import br.com.bellato.gerenciador_fifa.model.RivalidadeUsuario;
import br.com.bellato.gerenciador_fifa.model.User;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaUsuarioRepository;
import br.com.bellato.gerenciador_fifa.repository.HistoricoConfrontoUsuarioRepository;
import br.com.bellato.gerenciador_fifa.repository.RivalidadeUsuarioRepository;
import br.com.bellato.gerenciador_fifa.repository.UserRepository;
import br.com.bellato.gerenciador_fifa.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstatisticaUsuarioRepository estatisticaUsuarioRepository;

    @Autowired
    private RivalidadeUsuarioRepository rivalidadeUsuarioRepository;

    @Autowired
    private HistoricoConfrontoUsuarioRepository historicoConfrontoUsuarioRepository;

    @Transactional(readOnly = true)
    public UserResponseDTO obterUsuarioAtual() {
        User user = obterEntidadeAtual();
        return UserMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO obterPorId(Long id) {
        User user = userRepository.findByIdComCompetitor(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
        return UserMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public User obterEntidadeAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return userRepository.findByIdComCompetitor(principal.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<UserSearchItemDTO> buscar(String q, int page, int size) {
        return buscar(q, page, size, null);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<UserSearchItemDTO> buscar(String q, int page, int size, Long excludeUserId) {
        int pagina = Math.max(0, page);
        int tamanho = Math.min(Math.max(1, size), 50);
        Pageable pageable = PageRequest.of(pagina, tamanho);
        String termo = q != null ? q.trim() : "";
        Page<User> resultado = userRepository.buscar(termo, excludeUserId, pageable);

        List<UserSearchItemDTO> content = resultado.getContent().stream().map(u -> {
            UserSearchItemDTO dto = new UserSearchItemDTO();
            dto.setId(u.getUserId());
            dto.setUsername(u.getUsername());
            if (u.getCompetitor() != null) {
                dto.setDisplayName(u.getCompetitor().getDisplayName());
            } else {
                dto.setDisplayName(u.getUsername());
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageResponseDTO<>(content, pagina, tamanho, resultado.getTotalElements(), resultado.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UsuarioPerfilResponseDTO obterPerfil(Long userId) {
        User user = userRepository.findByIdComCompetitor(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + userId));

        EstatisticaUsuario stats = estatisticaUsuarioRepository.findByUsuarioIdComClubes(userId).orElse(null);

        UsuarioPerfilResponseDTO dto = new UsuarioPerfilResponseDTO();
        dto.setId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getCompetitor() != null ? user.getCompetitor().getDisplayName() : user.getUsername());
        dto.setCreatedAt(user.getCreatedAt());

        if (stats == null) {
            dto.setAproveitamento(0.0);
            dto.setMediaGols(0.0);
            dto.setClubesUsados(0);
            dto.setPartidasDisputadas(0);
            return dto;
        }

        dto.setCampeonatosDisputados(stats.getCampeonatosDisputados());
        dto.setCampeonatosVencidos(stats.getCampeonatosVencidos());
        dto.setViceCampeonatos(stats.getViceCampeonatos());
        dto.setVitorias(stats.getVitorias());
        dto.setEmpates(stats.getEmpates());
        dto.setDerrotas(stats.getDerrotas());
        dto.setPartidasDisputadas(stats.getPartidasDisputadas());
        dto.setGolsMarcados(stats.getGolsMarcados());
        dto.setGolsSofridos(stats.getGolsSofridos());
        dto.setSaldoGols(stats.getSaldoGols());
        dto.setMaiorSequenciaVitorias(stats.getMaiorSequenciaVitorias());
        dto.setMaiorSequenciaInvicta(stats.getMaiorSequenciaInvicta());
        dto.setClubesUsados(stats.getClubesUsados());
        dto.setDataUltimoCampeonato(stats.getDataUltimoCampeonato());
        dto.setAproveitamento(calcularAproveitamento(stats.getVitorias(), stats.getEmpates(), stats.getDerrotas()));
        dto.setMediaGols(calcularMediaGols(stats.getGolsMarcados(), stats.getVitorias(), stats.getEmpates(),
                stats.getDerrotas()));

        if (stats.getClubeFavorito() != null) {
            dto.setClubeFavorito(stats.getClubeFavorito().getNome());
            dto.setClubeFavoritoId(stats.getClubeFavorito().getClubeId());
        }
        if (stats.getClubeMaisTitulos() != null) {
            dto.setClubeMaisTitulos(stats.getClubeMaisTitulos().getNome());
            dto.setClubeMaisTitulosId(stats.getClubeMaisTitulos().getClubeId());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public UsuarioPerfilResponseDTO obterPerfilAtual() {
        return obterPerfil(obterEntidadeAtual().getUserId());
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<RivalidadeItemDTO> listarRivalidades(Long userId, String q, int page, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + userId));

        int pagina = Math.max(0, page);
        int tamanho = Math.min(Math.max(1, size), 50);
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "confrontos"));
        String termo = q != null ? q.trim() : "";

        Page<RivalidadeUsuario> resultado = rivalidadeUsuarioRepository.buscarPorUsuario(userId, termo, pageable);
        List<Long> ids = resultado.getContent().stream().map(RivalidadeUsuario::getRivalidadeUsuarioId).toList();

        Map<Long, RivalidadeUsuario> fetched = ids.isEmpty()
                ? Map.of()
                : rivalidadeUsuarioRepository.findAllByIdComUsuarios(ids).stream()
                        .collect(Collectors.toMap(RivalidadeUsuario::getRivalidadeUsuarioId, Function.identity()));

        List<RivalidadeItemDTO> content = new ArrayList<>();
        for (RivalidadeUsuario r : resultado.getContent()) {
            RivalidadeUsuario full = fetched.getOrDefault(r.getRivalidadeUsuarioId(), r);
            content.add(toRivalidadeItem(full, userId));
        }

        return new PageResponseDTO<>(content, pagina, tamanho, resultado.getTotalElements(), resultado.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<RivalidadeItemDTO> listarRivalidadesAtual(String q, int page, int size) {
        return listarRivalidades(obterEntidadeAtual().getUserId(), q, page, size);
    }

    @Transactional(readOnly = true)
    public RivalidadeDetalheDTO obterRivalidade(Long rivalidadeId) {
        RivalidadeUsuario r = rivalidadeUsuarioRepository.findByIdComUsuarios(rivalidadeId)
                .orElseThrow(() -> new EntityNotFoundException("Rivalidade não encontrada com o ID: " + rivalidadeId));

        RivalidadeDetalheDTO dto = new RivalidadeDetalheDTO();
        dto.setRivalidadeId(r.getRivalidadeUsuarioId());

        User a = r.getUsuarioA();
        User b = r.getUsuarioB();
        dto.setUsuarioAId(a.getUserId());
        dto.setUsuarioAUsername(a.getUsername());
        dto.setUsuarioADisplayName(displayName(a));
        dto.setUsuarioBId(b.getUserId());
        dto.setUsuarioBUsername(b.getUsername());
        dto.setUsuarioBDisplayName(displayName(b));

        dto.setConfrontos(r.getConfrontos());
        dto.setVitoriasUsuarioA(r.getVitoriasUsuarioA());
        dto.setVitoriasUsuarioB(r.getVitoriasUsuarioB());
        dto.setEmpates(r.getEmpates());
        dto.setGolsUsuarioA(r.getGolsUsuarioA());
        dto.setGolsUsuarioB(r.getGolsUsuarioB());
        dto.setSaldoUsuarioA(r.getGolsUsuarioA() - r.getGolsUsuarioB());
        dto.setAproveitamentoUsuarioA(
                calcularAproveitamento(r.getVitoriasUsuarioA(), r.getEmpates(), r.getVitoriasUsuarioB()));
        dto.setAproveitamentoUsuarioB(
                calcularAproveitamento(r.getVitoriasUsuarioB(), r.getEmpates(), r.getVitoriasUsuarioA()));

        dto.setCampeonatosDisputados(r.getCampeonatosDisputados());
        dto.setTitulosUsuarioA(r.getTitulosUsuarioA());
        dto.setTitulosUsuarioB(r.getTitulosUsuarioB());

        if (r.getMaiorGoleadaAMargem() > 0) {
            dto.setMaiorGoleadaAGolsPro(r.getMaiorGoleadaAGolsPro());
            dto.setMaiorGoleadaAGolsContra(r.getMaiorGoleadaAGolsContra());
            dto.setMaiorGoleadaAMargem(r.getMaiorGoleadaAMargem());
            dto.setMaiorGoleadaAPlacar(formatarPlacarGoleada(
                    r.getMaiorGoleadaAClubePro(), r.getMaiorGoleadaAGolsPro(),
                    r.getMaiorGoleadaAGolsContra(), r.getMaiorGoleadaAClubeContra()));
            dto.setMaiorGoleadaACampeonatoNome(r.getMaiorGoleadaACampeonatoNome());
        }
        if (r.getMaiorGoleadaBMargem() > 0) {
            dto.setMaiorGoleadaBGolsPro(r.getMaiorGoleadaBGolsPro());
            dto.setMaiorGoleadaBGolsContra(r.getMaiorGoleadaBGolsContra());
            dto.setMaiorGoleadaBMargem(r.getMaiorGoleadaBMargem());
            dto.setMaiorGoleadaBPlacar(formatarPlacarGoleada(
                    r.getMaiorGoleadaBClubePro(), r.getMaiorGoleadaBGolsPro(),
                    r.getMaiorGoleadaBGolsContra(), r.getMaiorGoleadaBClubeContra()));
            dto.setMaiorGoleadaBCampeonatoNome(r.getMaiorGoleadaBCampeonatoNome());
        }

        dto.setMaiorSequenciaVitoriasA(r.getMaiorSequenciaVitoriasA());
        dto.setMaiorSequenciaVitoriasB(r.getMaiorSequenciaVitoriasB());
        dto.setSequenciaAtual(r.getSequenciaAtual());
        if (r.getSequenciaAtualUsuario() != null) {
            dto.setSequenciaAtualUserId(r.getSequenciaAtualUsuario().getUserId());
            dto.setSequenciaAtualUsername(r.getSequenciaAtualUsuario().getUsername());
        }

        dto.setDataUltimoConfronto(r.getDataUltimoConfronto());
        if (r.getUltimoVencedor() != null) {
            dto.setUltimoVencedorUserId(r.getUltimoVencedor().getUserId());
            dto.setUltimoVencedorUsername(r.getUltimoVencedor().getUsername());
        }

        List<HistoricoConfrontoUsuario> historicos = historicoConfrontoUsuarioRepository
                .listarPorParUsuarios(a.getUserId(), b.getUserId());
        historicos.sort((h1, h2) -> {
            if (h1.getDataFinalizacao() == null && h2.getDataFinalizacao() == null) {
                return 0;
            }
            if (h1.getDataFinalizacao() == null) {
                return 1;
            }
            if (h2.getDataFinalizacao() == null) {
                return -1;
            }
            return h2.getDataFinalizacao().compareTo(h1.getDataFinalizacao());
        });

        List<ConfrontoTimelineDTO> timeline = new ArrayList<>();
        for (HistoricoConfrontoUsuario h : historicos) {
            timeline.add(toTimelineItem(h, a.getUserId(), b.getUserId()));
        }
        dto.setLinhaDoTempo(timeline);
        dto.setCampeonatos(timeline);
        return dto;
    }

    private ConfrontoTimelineDTO toTimelineItem(HistoricoConfrontoUsuario h, Long userAId, Long userBId) {
        ConfrontoTimelineDTO item = new ConfrontoTimelineDTO();
        item.setHistoricoId(h.getHistoricoConfrontoUsuarioId());
        if (h.getCampeonato() != null) {
            item.setCampeonatoId(h.getCampeonato().getCampeonatoId());
        }
        item.setNomeCampeonato(h.getNomeCampeonato());
        item.setDataFinalizacao(h.getDataFinalizacao());
        item.setPartidasDisputadas(h.getPartidasDisputadas());

        Long partAUserId = h.getParticipanteA() != null && h.getParticipanteA().getUser() != null
                ? h.getParticipanteA().getUser().getUserId()
                : null;

        int golsA;
        int golsB;
        int goleadaA;
        int goleadaB;
        if (partAUserId != null && partAUserId.equals(userAId)) {
            golsA = h.getGolsParticipanteA();
            golsB = h.getGolsParticipanteB();
            goleadaA = h.getMaiorGoleadaGolsA();
            goleadaB = h.getMaiorGoleadaGolsB();
        } else {
            golsA = h.getGolsParticipanteB();
            golsB = h.getGolsParticipanteA();
            goleadaA = h.getMaiorGoleadaGolsB();
            goleadaB = h.getMaiorGoleadaGolsA();
        }

        item.setGolsParticipanteA(golsA);
        item.setGolsParticipanteB(golsB);
        item.setMaiorGoleadaGolsA(goleadaA);
        item.setMaiorGoleadaGolsB(goleadaB);

        if (h.getVencedor() != null && h.getVencedor().getUser() != null) {
            User campeao = h.getVencedor().getUser();
            item.setCampeaoUserId(campeao.getUserId());
            item.setCampeaoUsername(campeao.getUsername());
            item.setCampeaoDisplayName(displayName(campeao));
        }
        if (h.getClubeCampeao() != null) {
            item.setClubeCampeaoNome(h.getClubeCampeao().getNome());
        }
        return item;
    }

    private RivalidadeItemDTO toRivalidadeItem(RivalidadeUsuario r, Long viewerUserId) {
        boolean viewerIsA = r.getUsuarioA().getUserId().equals(viewerUserId);
        User rival = viewerIsA ? r.getUsuarioB() : r.getUsuarioA();
        int wins = viewerIsA ? r.getVitoriasUsuarioA() : r.getVitoriasUsuarioB();
        int losses = viewerIsA ? r.getVitoriasUsuarioB() : r.getVitoriasUsuarioA();
        int goalsFor = viewerIsA ? r.getGolsUsuarioA() : r.getGolsUsuarioB();
        int goalsAgainst = viewerIsA ? r.getGolsUsuarioB() : r.getGolsUsuarioA();

        RivalidadeItemDTO dto = new RivalidadeItemDTO();
        dto.setRivalidadeId(r.getRivalidadeUsuarioId());
        dto.setRivalUserId(rival.getUserId());
        dto.setRivalUsername(rival.getUsername());
        dto.setRivalDisplayName(displayName(rival));
        dto.setConfrontos(r.getConfrontos());
        dto.setVitorias(wins);
        dto.setDerrotas(losses);
        dto.setEmpates(r.getEmpates());
        dto.setGolsMarcados(goalsFor);
        dto.setGolsSofridos(goalsAgainst);
        dto.setSaldoGols(goalsFor - goalsAgainst);
        dto.setAproveitamento(calcularAproveitamento(wins, r.getEmpates(), losses));
        dto.setCampeonatosDisputados(r.getCampeonatosDisputados());
        dto.setDataUltimoConfronto(r.getDataUltimoConfronto());
        if (r.getUltimoVencedor() != null) {
            dto.setUltimoVencedorUserId(r.getUltimoVencedor().getUserId());
            dto.setUltimoVencedorUsername(r.getUltimoVencedor().getUsername());
        }
        return dto;
    }

    private static String formatarPlacarGoleada(String clubePro, int golsPro, int golsContra, String clubeContra) {
        String pro = clubePro != null && !clubePro.isBlank() ? clubePro : "Clube";
        String contra = clubeContra != null && !clubeContra.isBlank() ? clubeContra : "Clube";
        return pro + " " + golsPro + " × " + golsContra + " " + contra;
    }

    private static String displayName(User user) {
        if (user.getCompetitor() != null && user.getCompetitor().getDisplayName() != null) {
            return user.getCompetitor().getDisplayName();
        }
        return user.getUsername();
    }

    private static Double calcularAproveitamento(int wins, int draws, int losses) {
        int jogos = wins + draws + losses;
        if (jogos <= 0) {
            return 0.0;
        }
        return Math.round(((wins * 3.0 + draws) / (jogos * 3.0)) * 1000.0) / 10.0;
    }

    private static Double calcularMediaGols(int goalsFor, int wins, int draws, int losses) {
        int jogos = wins + draws + losses;
        if (jogos <= 0) {
            return 0.0;
        }
        return Math.round((goalsFor / (double) jogos) * 100.0) / 100.0;
    }
}
