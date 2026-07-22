package br.com.bellato.gerenciador_fifa.service.finalizacao;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;

/**
 * Política de evolução de ranks ao finalizar campeonatos (16 / 32 / 64 clubes).
 * Movimentação limitada; campeão protegido nunca rebaixa neste campeonato.
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

        if (campeao) {
            delta = -deltaPromocaoCampeao(atual);
        } else if (vice) {
            delta = -Math.min(2, MAX_MOVIMENTO);
        } else {
            delta = deltaPorDesempenho(atual, faseAlcancada, totalRodadas, posicaoFinal);
        }

        if (protegido && delta > 0) {
            delta = 0;
        }

        delta = Math.max(-MAX_MOVIMENTO, Math.min(MAX_MOVIMENTO, delta));

        if (delta < 0) {
            ClubRank promovido = atual.promover(-delta);
            // Limite de teto por rank de origem do campeão (espírito da regra)
            if (campeao) {
                ClubRank teto = tetoCampeao(atual);
                if (promovido.getNivel() < teto.getNivel()) {
                    return teto;
                }
            }
            return promovido;
        }
        if (delta > 0) {
            // Rank S só cai após desempenho muito ruim (já refletido em deltaPorDesempenho)
            return atual.rebaixar(delta);
        }
        return atual;
    }

    private static int deltaPromocaoCampeao(ClubRank atual) {
        return switch (atual) {
            case E -> 3; // até B
            case D -> 3; // até A
            case C -> 2; // até A
            case B -> 1; // até S
            case A -> 1; // até S
            case S -> 0;
        };
    }

    private static ClubRank tetoCampeao(ClubRank atual) {
        return switch (atual) {
            case E -> ClubRank.B;
            case D, C -> ClubRank.A;
            case B, A, S -> ClubRank.S;
        };
    }

    /**
     * delta negativo = promoção; positivo = rebaixamento.
     */
    private static int deltaPorDesempenho(ClubRank atual, int faseAlcancada, int totalRodadas, int posicaoFinal) {
        int rodadas = Math.max(1, totalRodadas);
        double performance = Math.min(1.0, (double) Math.max(0, faseAlcancada) / rodadas);
        double esperado = desempenhoEsperado(atual);
        double diff = performance - esperado;

        // Ajuste fino pela posição relativa (pior colocação pressiona rebaixamento)
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
