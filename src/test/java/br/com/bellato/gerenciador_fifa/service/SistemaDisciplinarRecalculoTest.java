package br.com.bellato.gerenciador_fifa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.bellato.gerenciador_fifa.enums.MotivoSuspensao;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartidaEvento;
import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;
import br.com.bellato.gerenciador_fifa.model.CampeonatoSuspensao;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoSuspensaoRepository;

/**
 * Recálculo idempotente: transferências, cumprimento e herança entre campeonatos.
 */
@ExtendWith(MockitoExtension.class)
class SistemaDisciplinarRecalculoTest {

    private static final String IDENTIDADE = "G-99";

    @Mock
    private CampeonatoSuspensaoRepository campeonatoSuspensaoRepository;

    @InjectMocks
    private SistemaDisciplinarService service;

    private final AtomicLong seq = new AtomicLong(1);

    @BeforeEach
    void stubBasico() {
        lenient().when(campeonatoSuspensaoRepository.findByCampeonatoCampeonatoIdAndHerdadaTrue(anyLong()))
                .thenReturn(new ArrayList<>());
    }

    // --- TESTE 7 ---
    @Test
    void teste7_jogadorSuspensoTransferido_continuaSuspensoNoNovoClubeAteCumprir() {
        Campeonato campeonato = campeonato(1L);
        CampeonatoClube liverpool = clube(10L, "Liverpool");
        CampeonatoClube city = clube(20L, "City");

        // Partidas já criadas no início (IDs baixos); transferência gera vínculo com ID alto
        CampeonatoPartida p1 = partida(1L, liverpool, clube(11L, "Rival1"), StatusPartida.FINALIZADA, 1);
        CampeonatoPartida p2 = partida(2L, city, clube(21L, "Rival2"), StatusPartida.FINALIZADA, 2);

        CampeonatoAtleta vinculoLiverpool = atleta(100L, liverpool, IDENTIDADE, false);
        CampeonatoAtleta vinculoCity = atleta(500L, city, IDENTIDADE, true); // ID > partida IDs

        // Vermelho na partida 1 (ainda no Liverpool)
        adicionarEvento(p1, vinculoLiverpool, TipoEventoPartida.CARTAO_VERMELHO);

        montarRodadas(campeonato, p1, p2);

        ArgumentCaptor<List<CampeonatoSuspensao>> captor = ArgumentCaptor.forClass(List.class);
        when(campeonatoSuspensaoRepository.saveAll(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.recalcularSuspensoes(campeonato, List.of(vinculoLiverpool, vinculoCity));

        List<CampeonatoSuspensao> salvas = captor.getValue();
        // Suspensão gerada em p1 deve ter sido cumprida em p2 (clube atual = City)
        assertEquals(1, salvas.size());
        CampeonatoSuspensao s = salvas.get(0);
        assertEquals(IDENTIDADE, s.getIdentidade());
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO.getCodigo(), s.getMotivo());
        assertFalse(s.isAtiva(), "deve cumprir na partida do novo clube");
        assertEquals(p2.getCampeonatoPartidaId(), s.getPartidaCumprimento().getCampeonatoPartidaId());
    }

    // --- TESTE 8 (acúmulo + transferência) ---
    @Test
    void teste8_amareloAcumuladoSobreviveATransferencia() {
        Campeonato campeonato = campeonato(1L);
        CampeonatoClube liverpool = clube(10L, "Liverpool");
        CampeonatoClube city = clube(20L, "City");

        CampeonatoPartida p1 = partida(1L, liverpool, clube(11L, "Rival1"), StatusPartida.FINALIZADA, 1);
        CampeonatoPartida p2 = partida(2L, city, clube(21L, "Rival2"), StatusPartida.FINALIZADA, 2);
        CampeonatoPartida p3 = partida(3L, city, clube(22L, "Rival3"), StatusPartida.FINALIZADA, 3);

        CampeonatoAtleta vLiverpool = atleta(100L, liverpool, IDENTIDADE, false);
        CampeonatoAtleta vCity = atleta(900L, city, IDENTIDADE, true);

        adicionarEvento(p1, vLiverpool, TipoEventoPartida.CARTAO_AMARELO);
        adicionarEvento(p2, vCity, TipoEventoPartida.CARTAO_AMARELO);

        montarRodadas(campeonato, p1, p2, p3);

        ArgumentCaptor<List<CampeonatoSuspensao>> captor = ArgumentCaptor.forClass(List.class);
        when(campeonatoSuspensaoRepository.saveAll(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.recalcularSuspensoes(campeonato, List.of(vLiverpool, vCity));

        List<CampeonatoSuspensao> salvas = captor.getValue();
        assertEquals(1, salvas.size());
        CampeonatoSuspensao s = salvas.get(0);
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS.getCodigo(), s.getMotivo());
        assertFalse(s.isAtiva());
        assertEquals(p3.getCampeonatoPartidaId(), s.getPartidaCumprimento().getCampeonatoPartidaId());
    }

    // --- TESTE 9 / 11 / 12 (herança) ---
    @Test
    void teste9e11_suspensaoPendenteEHerdadaParaProximoCampeonato() {
        Campeonato novo = campeonato(2L);
        CampeonatoAtleta atletaNovo = atleta(50L, clube(30L, "NovoClube"), IDENTIDADE, true);

        Campeonato antigo = campeonato(1L);
        antigo.setStatus(StatusCampeonato.FINALIZADO);
        CampeonatoPartida origem = partida(77L, clube(10L, "Old"), clube(11L, "X"), StatusPartida.FINALIZADA, 1);

        CampeonatoSuspensao pendente = new CampeonatoSuspensao();
        pendente.setCampeonatoSuspensaoId(1L);
        pendente.setCampeonato(antigo);
        pendente.setIdentidade(IDENTIDADE);
        pendente.setPartidaOrigem(origem);
        pendente.setAtiva(true);
        pendente.setMotivo(MotivoSuspensao.CARTAO_VERMELHO.getCodigo());
        pendente.setHerdada(false);

        when(campeonatoSuspensaoRepository.findAtivasEmCampeonatosComStatus(
                eq(StatusCampeonato.FINALIZADO), eq(2L)))
                .thenReturn(List.of(pendente));

        ArgumentCaptor<List<CampeonatoSuspensao>> captor = ArgumentCaptor.forClass(List.class);
        when(campeonatoSuspensaoRepository.saveAll(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.herdarSuspensoesPendentes(novo, List.of(atletaNovo));

        assertFalse(pendente.isAtiva(), "origem deve ser marcada como transferida");
        List<List<CampeonatoSuspensao>> saves = captor.getAllValues();
        assertTrue(saves.size() >= 2);
        List<CampeonatoSuspensao> copias = saves.get(saves.size() - 1);
        assertEquals(1, copias.size());
        assertTrue(copias.get(0).isHerdada());
        assertTrue(copias.get(0).isAtiva());
        assertEquals(IDENTIDADE, copias.get(0).getIdentidade());
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO.getCodigo(), copias.get(0).getMotivo());
    }

    // --- TESTE 10 ---
    @Test
    void teste10_apenasAmareloIsolado_naoGeraSuspensaoParaHerdar() {
        Campeonato campeonato = campeonato(1L);
        CampeonatoClube clube = clube(10L, "Time");
        CampeonatoPartida p1 = partida(1L, clube, clube(11L, "Rival"), StatusPartida.FINALIZADA, 1);
        CampeonatoAtleta atleta = atleta(100L, clube, IDENTIDADE, true);
        adicionarEvento(p1, atleta, TipoEventoPartida.CARTAO_AMARELO);
        montarRodadas(campeonato, p1);

        service.recalcularSuspensoes(campeonato, List.of(atleta));

        verify(campeonatoSuspensaoRepository, never()).saveAll(any());
    }

    // --- TESTE 12 ---
    @Test
    void teste12_segundoAmareloNaFinal_geraSuspensaoAtiva() {
        Campeonato campeonato = campeonato(1L);
        CampeonatoClube clube = clube(10L, "Time");
        CampeonatoPartida semi = partida(1L, clube, clube(11L, "A"), StatusPartida.FINALIZADA, 1);
        CampeonatoPartida finale = partida(2L, clube, clube(12L, "B"), StatusPartida.FINALIZADA, 2);
        CampeonatoAtleta atleta = atleta(100L, clube, IDENTIDADE, true);

        adicionarEvento(semi, atleta, TipoEventoPartida.CARTAO_AMARELO);
        adicionarEvento(finale, atleta, TipoEventoPartida.CARTAO_AMARELO);
        montarRodadas(campeonato, semi, finale);

        ArgumentCaptor<List<CampeonatoSuspensao>> captor = ArgumentCaptor.forClass(List.class);
        when(campeonatoSuspensaoRepository.saveAll(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.recalcularSuspensoes(campeonato, List.of(atleta));

        List<CampeonatoSuspensao> salvas = captor.getValue();
        assertEquals(1, salvas.size());
        assertTrue(salvas.get(0).isAtiva(), "não há partida seguinte para cumprir");
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS.getCodigo(), salvas.get(0).getMotivo());
    }

    private Campeonato campeonato(Long id) {
        Campeonato c = new Campeonato();
        c.setCampeonatoId(id);
        c.setRodadas(new ArrayList<>());
        return c;
    }

    private CampeonatoClube clube(Long id, String nome) {
        CampeonatoClube c = new CampeonatoClube();
        c.setCampeonatoClubeId(id);
        c.setNome(nome);
        return c;
    }

    private CampeonatoAtleta atleta(Long id, CampeonatoClube clube, String identidade, boolean ativo) {
        CampeonatoAtleta a = new CampeonatoAtleta();
        a.setCampeonatoAtletaId(id);
        a.setCampeonatoClube(clube);
        a.setIdentidade(identidade);
        a.setNome("Jogador");
        a.setSobrenome("Teste");
        a.setAtivo(ativo);
        return a;
    }

    private CampeonatoPartida partida(
            Long id,
            CampeonatoClube mandante,
            CampeonatoClube visitante,
            StatusPartida status,
            int ordem) {
        CampeonatoPartida p = new CampeonatoPartida();
        p.setCampeonatoPartidaId(id);
        p.setClubeMandante(mandante);
        p.setClubeVisitante(visitante);
        p.setStatus(status);
        p.setOrdem(ordem);
        p.setEventos(new ArrayList<>());
        return p;
    }

    private void adicionarEvento(CampeonatoPartida partida, CampeonatoAtleta atleta, TipoEventoPartida tipo) {
        CampeonatoPartidaEvento e = new CampeonatoPartidaEvento();
        e.setCampeonatoPartidaEventoId(seq.getAndIncrement());
        e.setPartida(partida);
        e.setAtleta(atleta);
        e.setTipo(tipo);
        e.setOrdem(partida.getEventos().size() + 1);
        partida.getEventos().add(e);
    }

    private void montarRodadas(Campeonato campeonato, CampeonatoPartida... partidas) {
        CampeonatoRodada rodada = new CampeonatoRodada();
        rodada.setCampeonatoRodadaId(1L);
        rodada.setNumeroRodada(1);
        rodada.setCampeonato(campeonato);
        rodada.setPartidas(new ArrayList<>());
        for (CampeonatoPartida p : partidas) {
            p.setCampeonatoRodada(rodada);
            rodada.getPartidas().add(p);
        }
        campeonato.setRodadas(List.of(rodada));
    }
}
