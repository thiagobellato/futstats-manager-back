package br.com.bellato.gerenciador_fifa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.com.bellato.gerenciador_fifa.dto.campeonato.PartidaEventoRequestDTO;
import br.com.bellato.gerenciador_fifa.enums.MotivoSuspensao;
import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.service.SistemaDisciplinarService.ContagemPartida;
import br.com.bellato.gerenciador_fifa.service.SistemaDisciplinarService.PendenteSuspensao;

/**
 * Cobertura dos cenários disciplinares (histórico ≠ acúmulo ≠ suspensões).
 */
class SistemaDisciplinarServiceTest {

    private static final String ID = "G-1";
    private final SistemaDisciplinarService service = new SistemaDisciplinarService();

    // --- TESTE 1 ---
    @Test
    void teste1_umAmarelo_permaneceDisponivelSemSuspensao() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(1L), fila, pendentes);

        assertEquals(1, fila.get(ID));
        assertTrue(pendentes.isEmpty());
    }

    // --- TESTE 2 ---
    @Test
    void teste2_amarelosEmPartidasDiferentes_geraSuspensaoPorAcumulo() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(1L), fila, pendentes);
        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(2L), fila, pendentes);

        assertEquals(0, fila.get(ID));
        assertEquals(1, pendentes.get(ID).size());
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS, pendentes.get(ID).peekFirst().motivo());
    }

    // --- TESTE 3 / 14 ---
    @Test
    void teste3e14_aposCumprirSuspensao_acumuladorZeraEHistoricoConceitualPermanece() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(1L), fila, pendentes);
        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(2L), fila, pendentes);

        PendenteSuspensao cumprida = SistemaDisciplinarService.pollProximaPunicao(pendentes.get(ID));
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS, cumprida.motivo());
        fila.put(ID, 0); // cumprimento de acúmulo zera a fila ativa

        assertTrue(pendentes.get(ID).isEmpty());
        assertEquals(0, fila.get(ID));

        // Novo amarelo inicia sequência nova (não soma aos históricos já convertidos)
        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(4L), fila, pendentes);
        assertEquals(1, fila.get(ID));
        assertTrue(pendentes.get(ID) == null || pendentes.get(ID).isEmpty());
    }

    // --- TESTE 4 / 13 ---
    @Test
    void teste4e13_doisAmarelosNaMesmaPartida_vermelhoAutomatico_naoEntramNoAcumulo() {
        Map<String, Integer> fila = new HashMap<>();
        fila.put(ID, 1); // amarelo histórico pendente
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(2, 1), partida(2L), fila, pendentes);

        assertEquals(1, fila.get(ID), "amarelo antigo permanece; os 2 da partida não entram na fila");
        assertEquals(1, pendentes.get(ID).size());
        assertEquals(MotivoSuspensao.SEGUNDO_AMARELO, pendentes.get(ID).peekFirst().motivo());
    }

    // --- TESTE 5 ---
    @Test
    void teste5_amareloMaisVermelhoDireto_amareloPermaneceNoAcumulo() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 1), partida(1L), fila, pendentes);

        assertEquals(1, fila.get(ID));
        assertEquals(1, pendentes.get(ID).size());
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO, pendentes.get(ID).peekFirst().motivo());
    }

    // --- TESTE 6 ---
    @Test
    void teste6_vermelhoDireto_geraSuspensao() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(0, 1), partida(1L), fila, pendentes);

        assertEquals(0, fila.getOrDefault(ID, 0));
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO, pendentes.get(ID).peekFirst().motivo());
    }

    // --- TESTE 8 / 10 ---
    @Test
    void teste8e10_amareloPendenteMaisAmareloEVermelhoDireto_duasSuspensoesIndependentes() {
        Map<String, Integer> fila = new HashMap<>();
        fila.put(ID, 1);
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 1), partida(2L), fila, pendentes);

        assertEquals(0, fila.get(ID));
        assertEquals(2, pendentes.get(ID).size());
        Deque<PendenteSuspensao> d = pendentes.get(ID);
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS, d.pollFirst().motivo());
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO, d.pollFirst().motivo());
    }

    // --- TESTE 9 ---
    @Test
    void teste9_amareloAntigoSobreviveAVermelhoDireto_eFechaAcumuloDepois() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(1L), fila, pendentes);
        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(0, 1), partida(2L), fila, pendentes);

        assertEquals(1, fila.get(ID));
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO, pendentes.get(ID).peekFirst().motivo());

        SistemaDisciplinarService.pollProximaPunicao(pendentes.get(ID));

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(4L), fila, pendentes);
        assertEquals(0, fila.get(ID));
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS, pendentes.get(ID).peekFirst().motivo());
    }

    @Test
    void consumoPriorizaAcumuloDeAmarelosSobreVermelho() {
        Deque<PendenteSuspensao> fila = new ArrayDeque<>();
        CampeonatoPartida p = partida(1L);
        fila.addLast(new PendenteSuspensao(p, MotivoSuspensao.CARTAO_VERMELHO, false));
        fila.addLast(new PendenteSuspensao(p, MotivoSuspensao.ACUMULO_AMARELOS, false));

        PendenteSuspensao primeira = SistemaDisciplinarService.pollProximaPunicao(fila);
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS, primeira.motivo());
        PendenteSuspensao segunda = SistemaDisciplinarService.pollProximaPunicao(fila);
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO, segunda.motivo());
        assertNull(SistemaDisciplinarService.pollProximaPunicao(fila));
    }

    // --- TESTE 2 (estado da partida) / 17 ---
    @Test
    void teste17_doisAmarelosSemVermelho_insereVermelhoAutomaticoEPermiteSalvar() {
        List<PartidaEventoRequestDTO> eventos = List.of(
                evento(TipoEventoPartida.CARTAO_AMARELO, 10L),
                evento(TipoEventoPartida.CARTAO_AMARELO, 10L),
                evento(TipoEventoPartida.GOL, 10L));

        List<PartidaEventoRequestDTO> normalizados = service.recalcularEstadoDisciplinarDaPartida(eventos);

        long amarelos = normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_AMARELO).count();
        long vermelhos = normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_VERMELHO).count();
        long gols = normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.GOL).count();

        assertEquals(2, amarelos);
        assertEquals(1, vermelhos);
        assertEquals(1, gols);

        List<CampeonatoAtleta> atletas = normalizados.stream().map(e -> atleta(e.getCampeonatoAtletaId())).toList();
        service.validarEventosDisciplinares(normalizados, atletas); // não deve lançar
    }

    // --- TESTE 15 / 16 ---
    @Test
    void teste15e16_ordemDosEventosIrrelevante_expulsoPodeTerGolPosterior() {
        List<PartidaEventoRequestDTO> foraDeOrdem = List.of(
                evento(TipoEventoPartida.GOL, 10L),
                evento(TipoEventoPartida.CARTAO_VERMELHO, 10L),
                evento(TipoEventoPartida.ASSISTENCIA, 10L),
                evento(TipoEventoPartida.CARTAO_AMARELO, 10L));

        List<PartidaEventoRequestDTO> normalizados = service.recalcularEstadoDisciplinarDaPartida(foraDeOrdem);
        List<CampeonatoAtleta> atletas = normalizados.stream().map(e -> atleta(e.getCampeonatoAtletaId())).toList();

        service.validarEventosDisciplinares(normalizados, atletas);

        assertEquals(1, normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_AMARELO).count());
        assertEquals(1, normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_VERMELHO).count());
        assertEquals(1, normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.GOL).count());
        assertEquals(1, normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.ASSISTENCIA).count());
    }

    // --- TESTE 17 (remoção do 2º amarelo) ---
    @Test
    void teste17_removerSegundoAmarelo_removeNecessidadeDeVermelhoAutomatico() {
        List<PartidaEventoRequestDTO> comDoisAmarelos = List.of(
                evento(TipoEventoPartida.CARTAO_AMARELO, 10L),
                evento(TipoEventoPartida.CARTAO_AMARELO, 10L),
                evento(TipoEventoPartida.CARTAO_VERMELHO, 10L));

        List<PartidaEventoRequestDTO> aposRemoverUmAmarelo = List.of(
                evento(TipoEventoPartida.CARTAO_AMARELO, 10L));

        List<PartidaEventoRequestDTO> normalizados = service.recalcularEstadoDisciplinarDaPartida(aposRemoverUmAmarelo);

        assertEquals(1, normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_AMARELO).count());
        assertEquals(0, normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_VERMELHO).count());

        // Estado intermediário inválido 2A sem V continua sendo corrigido
        List<PartidaEventoRequestDTO> soDoisAmarelos = service.recalcularEstadoDisciplinarDaPartida(
                List.of(evento(TipoEventoPartida.CARTAO_AMARELO, 10L), evento(TipoEventoPartida.CARTAO_AMARELO, 10L)));
        assertEquals(1, soDoisAmarelos.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_VERMELHO).count());

        // 2A+1V permanece consistente
        List<PartidaEventoRequestDTO> consistente = service.recalcularEstadoDisciplinarDaPartida(comDoisAmarelos);
        assertEquals(2, consistente.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_AMARELO).count());
        assertEquals(1, consistente.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_VERMELHO).count());
    }

    @Test
    void naoPermiteDoisVermelhosNaMesmaPartida() {
        List<PartidaEventoRequestDTO> eventos = List.of(
                evento(TipoEventoPartida.CARTAO_VERMELHO, 10L),
                evento(TipoEventoPartida.CARTAO_VERMELHO, 10L));
        List<PartidaEventoRequestDTO> normalizados = service.recalcularEstadoDisciplinarDaPartida(eventos);
        assertEquals(1, normalizados.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_VERMELHO).count());

        List<CampeonatoAtleta> atletas = List.of(atleta(10L), atleta(10L));
        assertThrows(CampeonatoBusinessException.class,
                () -> service.validarEventosDisciplinares(eventos, atletas));
    }

    @Test
    void teste18_normalizacaoNaoDuplicaVermelhoAutomaticoEmRefresh() {
        List<PartidaEventoRequestDTO> jaComAuto = List.of(
                evento(TipoEventoPartida.CARTAO_AMARELO, 10L),
                evento(TipoEventoPartida.CARTAO_AMARELO, 10L),
                evento(TipoEventoPartida.CARTAO_VERMELHO, 10L));

        List<PartidaEventoRequestDTO> primeira = service.recalcularEstadoDisciplinarDaPartida(jaComAuto);
        List<PartidaEventoRequestDTO> segunda = service.recalcularEstadoDisciplinarDaPartida(primeira);
        List<PartidaEventoRequestDTO> terceira = service.recalcularEstadoDisciplinarDaPartida(segunda);

        assertEquals(2, terceira.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_AMARELO).count());
        assertEquals(1, terceira.stream().filter(e -> e.getTipo() == TipoEventoPartida.CARTAO_VERMELHO).count());
        assertEquals(terceira.size(), segunda.size());
    }

    private static CampeonatoPartida partida(Long id) {
        CampeonatoPartida p = new CampeonatoPartida();
        p.setCampeonatoPartidaId(id);
        return p;
    }

    private static PartidaEventoRequestDTO evento(TipoEventoPartida tipo, Long atletaId) {
        PartidaEventoRequestDTO e = new PartidaEventoRequestDTO();
        e.setTipo(tipo);
        e.setCampeonatoAtletaId(atletaId);
        return e;
    }

    private static CampeonatoAtleta atleta(Long id) {
        CampeonatoAtleta a = new CampeonatoAtleta();
        a.setCampeonatoAtletaId(id);
        a.setNome("Jogador");
        a.setSobrenome(String.valueOf(id));
        return a;
    }
}
