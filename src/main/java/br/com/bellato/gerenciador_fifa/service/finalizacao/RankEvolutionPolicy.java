package br.com.bellato.gerenciador_fifa.service.finalizacao;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;

/**
 * Política de evolução de ranks ao finalizar campeonatos (16 / 32 / 64 clubes).
 * <p>
 * A posição usada é a da classificação estatística (pontos, saldo, etc.),
 * independente de quem foi campeão do mata-mata.
 * O campeão recebe um bônus de +1 promoção após a regra normal.
 * Campeão protegido nunca rebaixa neste campeonato.
 */
public final class RankEvolutionPolicy {

    private static final int MAX_MOVIMENTO = 3;

    private RankEvolutionPolicy() {
    }

    public static ClubRank calcularNovoRank(
            CampeonatoClube snapshot,
            int posicaoFinal,
            int faseAlcancada,
            int totalRodadas,
            boolean campeao,
            boolean vice,
            boolean protegido) {

        ClubRank atual = snapshot.getRank() != null ? snapshot.getRank() : ClubRank.E;
        int delta;

        if (vice && !campeao) {
            delta = -Math.min(2, MAX_MOVIMENTO);
        } else {
            // Campeão e demais: evolução pela classificação / desempenho
            delta = deltaPorDesempenho(atual, faseAlcancada, totalRodadas, posicaoFinal);
        }

        if (protegido && delta > 0) {
            delta = 0;
        }

        delta = Math.max(-MAX_MOVIMENTO, Math.min(MAX_MOVIMENTO, delta));

        ClubRank resultado;
        if (delta < 0) {
            resultado = atual.promover(-delta);
        } else if (delta > 0) {
            resultado = atual.rebaixar(delta);
        } else {
            resultado = atual;
        }

        // Bônus do título: +1 nível após a regra normal (S permanece S)
        if (campeao) {
            resultado = resultado.promover(1);
        }

        return resultado;
    }

    /**
     * delta negativo = promoção; positivo = rebaixamento.
     */
    private static int deltaPorDesempenho(ClubRank atual, int faseAlcancada, int totalRodadas, int posicaoFinal) {
        int rodadas = Math.max(1, totalRodadas);
        double performance = Math.min(1.0, (double) Math.max(0, faseAlcancada) / rodadas);
        double esperado = desempenhoEsperado(atual);
        double diff = performance - esperado;

        // Ajuste fino pela posição relativa na classificação (pior colocação pressiona rebaixamento)
        if (posicaoFinal > rodadas * 4) {
            diff -= 0.15;
        }

        if (atual == ClubRank.S) {
            // S só perde após campanha claramente ruim (saída precoce)
            if (faseAlcancada <= 1 && diff < -0.35) {
                return 1;
            }
            return 0;
        }

        if (diff >= 0.35) {
            return -2;
        }
        if (diff >= 0.18) {
            return -1;
        }
        if (diff <= -0.45) {
            return 2;
        }
        if (diff <= -0.22) {
            return 1;
        }
        return 0;
    }

    private static double desempenhoEsperado(ClubRank rank) {
        return switch (rank) {
            case S -> 0.85;
            case A -> 0.70;
            case B -> 0.55;
            case C -> 0.40;
            case D -> 0.25;
            case E -> 0.15;
        };
    }
}
