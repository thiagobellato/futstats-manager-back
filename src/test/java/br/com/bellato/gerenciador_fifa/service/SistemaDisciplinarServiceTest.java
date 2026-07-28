package br.com.bellato.gerenciador_fifa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.com.bellato.gerenciador_fifa.enums.MotivoSuspensao;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.service.SistemaDisciplinarService.ContagemPartida;
import br.com.bellato.gerenciador_fifa.service.SistemaDisciplinarService.PendenteSuspensao;

/**
 * Cenários A–E da fila disciplinar (histórico vs acúmulo vs suspensões).
 */
class SistemaDisciplinarServiceTest {

    private static final String ID = "G-1";

    @Test
    void cenarioA_doisAmarelosEmJogosDiferentesGeraSuspensao() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();
        CampeonatoPartida p1 = partida(1L);
        CampeonatoPartida p2 = partida(2L);

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), p1, fila, pendentes);
        assertEquals(1, fila.get(ID));
        assertTrue(pendentes.isEmpty());

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), p2, fila, pendentes);
        assertEquals(0, fila.get(ID));
        assertEquals(1, pendentes.get(ID).size());
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS, pendentes.get(ID).peekFirst().motivo());
    }

    @Test
    void cenarioB_vermelhoDiretoNaoZeraAmareloAcumulado() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(1L), fila, pendentes);
        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(0, 1), partida(2L), fila, pendentes);

        assertEquals(1, fila.get(ID));
        assertEquals(1, pendentes.get(ID).size());
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO, pendentes.get(ID).peekFirst().motivo());

        // Cumprir vermelho NÃO zera amarelos
        PendenteSuspensao cumprida = SistemaDisciplinarService.pollProximaPunicao(pendentes.get(ID));
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO, cumprida.motivo());
        // fila permanece 1

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 0), partida(4L), fila, pendentes);
        assertEquals(0, fila.get(ID));
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS, pendentes.get(ID).peekFirst().motivo());
    }

    @Test
    void cenarioC_amareloMaisVermelhoDiretoNaMesmaPartidaGeraDuasSuspensoes() {
        Map<String, Integer> fila = new HashMap<>();
        fila.put(ID, 1); // amarelo do jogo 1
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(1, 1), partida(2L), fila, pendentes);

        assertEquals(0, fila.get(ID));
        assertEquals(2, pendentes.get(ID).size());
        Deque<PendenteSuspensao> d = pendentes.get(ID);
        assertEquals(MotivoSuspensao.ACUMULO_AMARELOS, d.pollFirst().motivo());
        assertEquals(MotivoSuspensao.CARTAO_VERMELHO, d.pollFirst().motivo());
    }

    @Test
    void cenarioD_doisAmarelosNaMesmaPartidaNaoEntramNaFila() {
        Map<String, Integer> fila = new HashMap<>();
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(2, 1), partida(1L), fila, pendentes);

        assertNull(fila.get(ID));
        assertEquals(1, pendentes.get(ID).size());
        assertEquals(MotivoSuspensao.SEGUNDO_AMARELO, pendentes.get(ID).peekFirst().motivo());
    }

    @Test
    void cenarioE_amareloAntigoSobreviveASegundoAmareloNaPartida() {
        Map<String, Integer> fila = new HashMap<>();
        fila.put(ID, 1);
        Map<String, Deque<PendenteSuspensao>> pendentes = new HashMap<>();

        SistemaDisciplinarService.aplicarCartoesNaFila(ID, new ContagemPartida(2, 1), partida(2L), fila, pendentes);

        assertEquals(1, fila.get(ID));
        assertEquals(MotivoSuspensao.SEGUNDO_AMARELO, pendentes.get(ID).peekFirst().motivo());

        // Após cumprir vermelho automático, fila permanece 1; novo amarelo gera acúmulo
        pendentes.get(ID).clear();
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

    private static CampeonatoPartida partida(Long id) {
        CampeonatoPartida p = new CampeonatoPartida();
        p.setCampeonatoPartidaId(id);
        return p;
    }
}
