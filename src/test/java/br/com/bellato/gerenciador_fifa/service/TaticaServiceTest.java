package br.com.bellato.gerenciador_fifa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaCompletoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaJogadorRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaSalvarRequestDTO;
import br.com.bellato.gerenciador_fifa.enums.FormacaoTatica;
import br.com.bellato.gerenciador_fifa.enums.TipoTaticaJogador;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.TaticaJogador;
import br.com.bellato.gerenciador_fifa.model.TaticaUsuarioClube;
import br.com.bellato.gerenciador_fifa.model.User;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.TaticaUsuarioClubeRepository;

@ExtendWith(MockitoExtension.class)
class TaticaServiceTest {

    @Mock
    private TaticaUsuarioClubeRepository taticaRepository;

    @Mock
    private AtletaRepository atletaRepository;

    private TaticaService taticaService;
    private ClubeServiceStub clubeServiceStub;
    private UserServiceStub userServiceStub;

    private User usuario1;
    private User usuario2;
    private Clube liverpool;
    private List<Atleta> elencoLiverpool;

    @BeforeEach
    void setUp() {
        taticaService = new TaticaService();
        clubeServiceStub = new ClubeServiceStub();
        userServiceStub = new UserServiceStub();

        ReflectionTestUtils.setField(taticaService, "taticaRepository", taticaRepository);
        ReflectionTestUtils.setField(taticaService, "atletaRepository", atletaRepository);
        ReflectionTestUtils.setField(taticaService, "clubeService", clubeServiceStub);
        ReflectionTestUtils.setField(taticaService, "userService", userServiceStub);

        usuario1 = new User();
        usuario1.setUserId(1L);
        usuario1.setUsername("thiago");

        usuario2 = new User();
        usuario2.setUserId(2L);
        usuario2.setUsername("michel");

        liverpool = new Clube();
        liverpool.setClubeId(10L);
        liverpool.setNome("Liverpool");
        liverpool.setSigla("LIV");
        clubeServiceStub.registrar(liverpool);

        elencoLiverpool = criarElenco(liverpool, 15);
        userServiceStub.setUsuario(usuario1);
    }

    @Test
    void teste1_usuarioCriaTaticaParaClube() {
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.empty());
        when(taticaRepository.save(any(TaticaUsuarioClube.class))).thenAnswer(inv -> {
            TaticaUsuarioClube t = inv.getArgument(0);
            t.setTaticaUsuarioClubeId(100L);
            return t;
        });

        TaticaSalvarRequestDTO request = criarRequestBasico();
        TaticaCompletoResponseDTO response = taticaService.salvar(10L, request);

        assertNotNull(response);
        assertEquals(FormacaoTatica.F_4_3_3, response.getFormacao());
        assertEquals("Pressionar alto", response.getAnotacoes());
        verify(taticaRepository).save(any(TaticaUsuarioClube.class));
    }

    @Test
    void teste2_mesmaCombinacaoUsuarioClubeReutilizaConfiguracao() {
        TaticaUsuarioClube existente = criarTaticaExistente(usuario1, liverpool);
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.of(existente));
        when(taticaRepository.save(existente)).thenReturn(existente);

        TaticaSalvarRequestDTO request = criarRequestBasico();
        request.setAnotacoes("Nova anotação");

        TaticaCompletoResponseDTO response = taticaService.salvar(10L, request);

        assertEquals(100L, response.getTaticaUsuarioClubeId());
        assertEquals("Nova anotação", response.getAnotacoes());
        assertEquals(1, existente.getJogadores().size());
    }

    @Test
    void teste3_outroUsuarioPossuiTaticaDiferenteParaMesmoClube() {
        TaticaUsuarioClube taticaMichel = criarTaticaExistente(usuario2, liverpool);
        taticaMichel.setTaticaUsuarioClubeId(200L);
        taticaMichel.setAnotacoes("Tática do Michel");

        userServiceStub.setUsuario(usuario2);
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(2L, 10L)).thenReturn(Optional.of(taticaMichel));

        TaticaCompletoResponseDTO response = taticaService.obterPorClube(10L);

        assertEquals(200L, response.getTaticaUsuarioClubeId());
        assertEquals("Tática do Michel", response.getAnotacoes());
    }

    @Test
    void teste4_usuarioNaoAcessaTaticaPrivadaDeOutro() {
        TaticaUsuarioClube taticaMichel = criarTaticaExistente(usuario2, liverpool);
        taticaMichel.setTaticaUsuarioClubeId(200L);

        when(taticaRepository.findByIdComDetalhes(200L)).thenReturn(Optional.of(taticaMichel));

        assertThrows(IllegalArgumentException.class, () -> taticaService.obterPorId(200L));
    }

    @Test
    void teste5_maximoDe11Titulares() {
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);

        TaticaSalvarRequestDTO request = new TaticaSalvarRequestDTO();
        request.setFormacao(FormacaoTatica.F_4_4_2);
        List<TaticaJogadorRequestDTO> jogadores = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            TaticaJogadorRequestDTO jr = new TaticaJogadorRequestDTO();
            jr.setAtletaId(elencoLiverpool.get(i).getAtletaId());
            jr.setTipo(TipoTaticaJogador.TITULAR);
            jr.setPosicaoX(50.0);
            jr.setPosicaoY(50.0);
            jogadores.add(jr);
        }
        request.setJogadores(jogadores);

        assertThrows(IllegalArgumentException.class, () -> taticaService.salvar(10L, request));
    }

    @Test
    void teste6_movimentacaoTitularReserva() {
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.empty());
        when(taticaRepository.save(any(TaticaUsuarioClube.class))).thenAnswer(inv -> inv.getArgument(0));

        TaticaSalvarRequestDTO request = new TaticaSalvarRequestDTO();
        request.setFormacao(FormacaoTatica.F_4_4_2);

        TaticaJogadorRequestDTO titular = new TaticaJogadorRequestDTO();
        titular.setAtletaId(elencoLiverpool.get(0).getAtletaId());
        titular.setTipo(TipoTaticaJogador.TITULAR);
        titular.setPosicaoX(50.0);
        titular.setPosicaoY(10.0);

        TaticaJogadorRequestDTO reserva = new TaticaJogadorRequestDTO();
        reserva.setAtletaId(elencoLiverpool.get(1).getAtletaId());
        reserva.setTipo(TipoTaticaJogador.RESERVA);
        reserva.setOrdemReserva(0);

        request.setJogadores(List.of(titular, reserva));

        TaticaCompletoResponseDTO response = taticaService.salvar(10L, request);

        assertEquals(1, response.getTitulares().size());
        assertEquals(1, response.getReservas().size());
        assertEquals(elencoLiverpool.get(0).getAtletaId(), response.getTitulares().get(0).getAtletaId());
        assertEquals(elencoLiverpool.get(1).getAtletaId(), response.getReservas().get(0).getAtletaId());
    }

    @Test
    void teste7_salvarCoordenadas() {
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.empty());
        when(taticaRepository.save(any(TaticaUsuarioClube.class))).thenAnswer(inv -> inv.getArgument(0));

        TaticaSalvarRequestDTO request = new TaticaSalvarRequestDTO();
        request.setFormacao(FormacaoTatica.F_4_4_2);

        TaticaJogadorRequestDTO titular = new TaticaJogadorRequestDTO();
        titular.setAtletaId(elencoLiverpool.get(0).getAtletaId());
        titular.setTipo(TipoTaticaJogador.TITULAR);
        titular.setPosicaoX(35.5);
        titular.setPosicaoY(72.3);
        request.setJogadores(List.of(titular));

        TaticaCompletoResponseDTO response = taticaService.salvar(10L, request);

        assertEquals(35.5, response.getTitulares().get(0).getPosicaoX());
        assertEquals(72.3, response.getTitulares().get(0).getPosicaoY());
    }

    @Test
    void teste8_recuperarConfiguracao() {
        TaticaUsuarioClube existente = criarTaticaExistente(usuario1, liverpool);
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.of(existente));

        TaticaCompletoResponseDTO response = taticaService.obterPorClube(10L);

        assertEquals(100L, response.getTaticaUsuarioClubeId());
        assertEquals(FormacaoTatica.F_4_4_2, response.getFormacao());
        assertEquals(1, response.getTitulares().size());
        assertEquals(15, response.getElenco().size());
    }

    @Test
    void teste9_alterarFormacaoSemApagarElenco() {
        TaticaUsuarioClube existente = criarTaticaExistente(usuario1, liverpool);
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.of(existente));
        when(taticaRepository.save(existente)).thenReturn(existente);

        TaticaSalvarRequestDTO request = criarRequestBasico();
        request.setFormacao(FormacaoTatica.F_3_5_2);

        TaticaCompletoResponseDTO response = taticaService.salvar(10L, request);

        assertEquals(FormacaoTatica.F_3_5_2, response.getFormacao());
        assertEquals(1, response.getTitulares().size());
    }

    @Test
    void teste10_salvarAnotacoes() {
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.empty());
        when(taticaRepository.save(any(TaticaUsuarioClube.class))).thenAnswer(inv -> inv.getArgument(0));

        TaticaSalvarRequestDTO request = new TaticaSalvarRequestDTO();
        request.setFormacao(FormacaoTatica.F_4_4_2);
        request.setAnotacoes("Salah corta para dentro. Gakpo pela esquerda.");

        TaticaCompletoResponseDTO response = taticaService.salvar(10L, request);

        assertEquals("Salah corta para dentro. Gakpo pela esquerda.", response.getAnotacoes());
    }

    @Test
    void teste11_atletaQueNaoPertenceAoClubeNaoPodeSerAdicionado() {
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);

        TaticaSalvarRequestDTO request = new TaticaSalvarRequestDTO();
        request.setFormacao(FormacaoTatica.F_4_4_2);

        TaticaJogadorRequestDTO jr = new TaticaJogadorRequestDTO();
        jr.setAtletaId(999L);
        jr.setTipo(TipoTaticaJogador.TITULAR);
        request.setJogadores(List.of(jr));

        assertThrows(IllegalArgumentException.class, () -> taticaService.salvar(10L, request));
    }

    @Test
    void teste12_obterPorClubeSemTaticaRetornaTemplateVazio() {
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.empty());

        TaticaCompletoResponseDTO response = taticaService.obterPorClube(10L);

        assertNull(response.getTaticaUsuarioClubeId());
        assertEquals(10L, response.getClubeId());
        assertEquals(15, response.getElenco().size());
        assertEquals(0, response.getTitulares().size());
    }

    @Test
    void teste13_salvarDuasVezesReutilizaJogadoresExistentes() {
        TaticaUsuarioClube existente = criarTaticaExistente(usuario1, liverpool);
        when(atletaRepository.findByClubeClubeId(10L)).thenReturn(elencoLiverpool);
        when(taticaRepository.findByUsuarioAndClube(1L, 10L)).thenReturn(Optional.of(existente));
        when(taticaRepository.save(existente)).thenReturn(existente);

        TaticaSalvarRequestDTO request = criarRequestBasico();
        taticaService.salvar(10L, request);

        Long jogadorIdAntes = existente.getJogadores().get(0).getTaticaJogadorId();
        assertNotNull(jogadorIdAntes);

        request.setAnotacoes("Segunda salvamento");
        taticaService.salvar(10L, request);

        assertEquals(1, existente.getJogadores().size());
        assertEquals(jogadorIdAntes, existente.getJogadores().get(0).getTaticaJogadorId());
        assertEquals(elencoLiverpool.get(0).getAtletaId(), existente.getJogadores().get(0).getAtleta().getAtletaId());
    }

    private TaticaSalvarRequestDTO criarRequestBasico() {
        TaticaSalvarRequestDTO request = new TaticaSalvarRequestDTO();
        request.setFormacao(FormacaoTatica.F_4_3_3);
        request.setAnotacoes("Pressionar alto");

        TaticaJogadorRequestDTO titular = new TaticaJogadorRequestDTO();
        titular.setAtletaId(elencoLiverpool.get(0).getAtletaId());
        titular.setTipo(TipoTaticaJogador.TITULAR);
        titular.setPosicaoX(50.0);
        titular.setPosicaoY(15.0);
        request.setJogadores(List.of(titular));

        return request;
    }

    private TaticaUsuarioClube criarTaticaExistente(User usuario, Clube clube) {
        TaticaUsuarioClube tatica = new TaticaUsuarioClube();
        tatica.setTaticaUsuarioClubeId(100L);
        tatica.setUsuario(usuario);
        tatica.setClube(clube);
        tatica.setFormacao(FormacaoTatica.F_4_4_2);
        tatica.setAnotacoes("Anotação existente");

        TaticaJogador jogador = new TaticaJogador();
        jogador.setTaticaJogadorId(500L);
        jogador.setAtleta(elencoLiverpool.get(0));
        jogador.setTipo(TipoTaticaJogador.TITULAR);
        jogador.setPosicaoX(50.0);
        jogador.setPosicaoY(10.0);
        jogador.setTaticaUsuarioClube(tatica);
        tatica.getJogadores().add(jogador);

        return tatica;
    }

    private List<Atleta> criarElenco(Clube clube, int quantidade) {
        List<Atleta> atletas = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            Atleta a = new Atleta();
            a.setAtletaId((long) (i + 1));
            a.setNome("Jogador" + i);
            a.setClube(clube);
            atletas.add(a);
        }
        return atletas;
    }

    static class ClubeServiceStub extends ClubeService {
        private final java.util.Map<Long, Clube> clubes = new java.util.HashMap<>();

        void registrar(Clube clube) {
            clubes.put(clube.getClubeId(), clube);
        }

        @Override
        public Clube obterPorId(long id) {
            Clube clube = clubes.get(id);
            if (clube == null) {
                throw new jakarta.persistence.EntityNotFoundException("Clube não encontrado com o ID: " + id);
            }
            return clube;
        }
    }

    static class UserServiceStub extends UserService {
        private User usuario;

        void setUsuario(User usuario) {
            this.usuario = usuario;
        }

        @Override
        public User obterEntidadeAtual() {
            return usuario;
        }
    }
}
