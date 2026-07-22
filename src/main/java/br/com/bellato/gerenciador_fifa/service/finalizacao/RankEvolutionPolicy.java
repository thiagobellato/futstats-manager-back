package br.com.bellato.gerenciador_fifa.service.finalizacao;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;

/**
 * Política de evolução de ranks ao finalizar campeonatos (16 / 32 / 64 clubes).
 * <p>
 * Ordem obrigatória:
 * <ol>
 *   <li>identifica faixa pela posição percentual na classificação;</li>
 *   <li>aplica evolução da classificação (±1 ou permanece);</li>
 *   <li>aplica regra especial do Rank S;</li>
 *   <li>restringe acesso ao Rank S (somente quem iniciou como A);</li>
 *   <li>aplica bônus do campeão (+1);</li>
 *   <li>protege o campeão vigente (nunca rebaixa).</li>
 * </ol>
 */
public final class RankEvolutionPolicy {

    private RankEvolutionPolicy() {
    }

    /**
     * Calcula o novo rank após a finalização do campeonato.
     *
     * @param snapshot     clube no campeonato (rank inicial)
     * @param posicaoFinal posição na classificação (1 = primeiro)
     * @param totalClubes  quantidade de clubes do campeonato (16 / 32 / 64)
     * @param campeao      se foi campeão do mata-mata
     * @param protegido    se é o campeão vigente (protegido de rebaixamento)
     */
    public static ClubRank calcularNovoRank(
            CampeonatoClube snapshot,
            int posicaoFinal,
            int totalClubes,
            boolean campeao,
            boolean protegido) {

        ClubRank rankInicial = snapshot.getRank() != null ? snapshot.getRank() : ClubRank.E;
        int total = Math.max(1, totalClubes);
        int posicao = Math.max(1, Math.min(posicaoFinal, total));
        Faixa faixa = identificarFaixa(posicao, total);

        // Etapa 2 — Evolução pela classificação
        ClubRank resultado = evoluirPorFaixa(rankInicial, faixa);

        // Etapa 3 — Regra especial do Rank S
        if (rankInicial == ClubRank.S) {
            resultado = aplicarRegraEspecialRankS(faixa);
        }

        // Etapa 4 — Restrição para chegar ao Rank S
        resultado = restringirAcessoRankS(rankInicial, resultado);

        // Etapa 5 — Bônus do campeão
        if (campeao) {
            resultado = resultado.promover(1);
            resultado = restringirAcessoRankS(rankInicial, resultado);
        }

        // Etapa 6 — Proteção do campeão vigente
        if (protegido && resultado.getNivel() > rankInicial.getNivel()) {
            resultado = rankInicial;
        }

        return resultado;
    }

    /**
     * Faixas por percentual (sem posições fixas):
     * <ul>
     *   <li>Faixa 1 — Top 25%</li>
     *   <li>Faixa 2 — Top 50% (após a Faixa 1)</li>
     *   <li>Faixa 3 — intermediária (até o início do rebaixamento)</li>
     *   <li>Faixa 4 — últimos 25%</li>
     * </ul>
     */
    static Faixa identificarFaixa(int posicao, int totalClubes) {
        int limiteFaixa1 = totalClubes / 4;
        int limiteFaixa2 = totalClubes / 2;
        int inicioFaixa4 = totalClubes - (totalClubes / 4) + 1;

        if (posicao <= limiteFaixa1) {
            return Faixa.TOP_25;
        }
        if (posicao <= limiteFaixa2) {
            return Faixa.TOP_50;
        }
        if (posicao < inicioFaixa4) {
            return Faixa.INTERMEDIARIA;
        }
        return Faixa.REBAIXAMENTO;
    }

    private static ClubRank evoluirPorFaixa(ClubRank rankInicial, Faixa faixa) {
        return switch (faixa) {
            case TOP_25 -> rankInicial.promover(1);
            case TOP_50, INTERMEDIARIA -> rankInicial;
            case REBAIXAMENTO -> rankInicial.rebaixar(1);
        };
    }

    private static ClubRank aplicarRegraEspecialRankS(Faixa faixa) {
        return switch (faixa) {
            case TOP_25 -> ClubRank.S;
            case TOP_50, INTERMEDIARIA -> ClubRank.A;
            case REBAIXAMENTO -> ClubRank.B;
        };
    }

    /**
     * Apenas quem iniciou como Rank A (ou já era S) pode terminar como Rank S.
     */
    private static ClubRank restringirAcessoRankS(ClubRank rankInicial, ClubRank resultado) {
        if (resultado == ClubRank.S
                && rankInicial != ClubRank.A
                && rankInicial != ClubRank.S) {
            return ClubRank.A;
        }
        return resultado;
    }

    enum Faixa {
        TOP_25,
        TOP_50,
        INTERMEDIARIA,
        REBAIXAMENTO
    }
}
