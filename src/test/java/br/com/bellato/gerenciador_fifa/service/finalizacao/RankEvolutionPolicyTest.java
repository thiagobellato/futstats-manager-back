package br.com.bellato.gerenciador_fifa.service.finalizacao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;

class RankEvolutionPolicyTest {

    @Test
    void faixasPercentuaisPara16Clubes() {
        assertEquals(RankEvolutionPolicy.Faixa.TOP_25, RankEvolutionPolicy.identificarFaixa(1, 16));
        assertEquals(RankEvolutionPolicy.Faixa.TOP_25, RankEvolutionPolicy.identificarFaixa(4, 16));
        assertEquals(RankEvolutionPolicy.Faixa.TOP_50, RankEvolutionPolicy.identificarFaixa(5, 16));
        assertEquals(RankEvolutionPolicy.Faixa.TOP_50, RankEvolutionPolicy.identificarFaixa(8, 16));
        assertEquals(RankEvolutionPolicy.Faixa.INTERMEDIARIA, RankEvolutionPolicy.identificarFaixa(9, 16));
        assertEquals(RankEvolutionPolicy.Faixa.INTERMEDIARIA, RankEvolutionPolicy.identificarFaixa(12, 16));
        assertEquals(RankEvolutionPolicy.Faixa.REBAIXAMENTO, RankEvolutionPolicy.identificarFaixa(13, 16));
        assertEquals(RankEvolutionPolicy.Faixa.REBAIXAMENTO, RankEvolutionPolicy.identificarFaixa(16, 16));
    }

    @Test
    void faixasPercentuaisPara32E64Clubes() {
        assertEquals(RankEvolutionPolicy.Faixa.TOP_25, RankEvolutionPolicy.identificarFaixa(8, 32));
        assertEquals(RankEvolutionPolicy.Faixa.TOP_50, RankEvolutionPolicy.identificarFaixa(9, 32));
        assertEquals(RankEvolutionPolicy.Faixa.REBAIXAMENTO, RankEvolutionPolicy.identificarFaixa(25, 32));

        assertEquals(RankEvolutionPolicy.Faixa.TOP_25, RankEvolutionPolicy.identificarFaixa(16, 64));
        assertEquals(RankEvolutionPolicy.Faixa.INTERMEDIARIA, RankEvolutionPolicy.identificarFaixa(33, 64));
        assertEquals(RankEvolutionPolicy.Faixa.REBAIXAMENTO, RankEvolutionPolicy.identificarFaixa(49, 64));
    }

    @Test
    void evolucaoPorFaixaSobeOuCaiUmNivel() {
        assertEquals(ClubRank.B, calcular(ClubRank.C, 1, 16, false, false));
        assertEquals(ClubRank.C, calcular(ClubRank.C, 5, 16, false, false));
        assertEquals(ClubRank.C, calcular(ClubRank.C, 10, 16, false, false));
        assertEquals(ClubRank.D, calcular(ClubRank.C, 14, 16, false, false));
        assertEquals(ClubRank.E, calcular(ClubRank.E, 16, 16, false, false));
    }

    @Test
    void regraEspecialRankS() {
        assertEquals(ClubRank.S, calcular(ClubRank.S, 2, 16, false, false));
        assertEquals(ClubRank.A, calcular(ClubRank.S, 6, 16, false, false));
        assertEquals(ClubRank.A, calcular(ClubRank.S, 10, 16, false, false));
        assertEquals(ClubRank.B, calcular(ClubRank.S, 15, 16, false, false));
    }

    @Test
    void apenasRankAPodeChegarEmS() {
        assertEquals(ClubRank.S, calcular(ClubRank.A, 1, 16, false, false));
        assertEquals(ClubRank.A, calcular(ClubRank.B, 1, 16, false, false));
        assertEquals(ClubRank.A, calcular(ClubRank.B, 1, 16, true, false));
        assertEquals(ClubRank.S, calcular(ClubRank.A, 6, 16, true, false));
        assertEquals(ClubRank.S, calcular(ClubRank.A, 1, 16, true, false));
    }

    @Test
    void bonusCampeaoAposRegraNormal() {
        assertEquals(ClubRank.C, calcular(ClubRank.E, 1, 16, true, false));
        assertEquals(ClubRank.A, calcular(ClubRank.C, 1, 16, true, false));
    }

    @Test
    void protecaoCampeaoVigenteNaoRebaixa() {
        assertEquals(ClubRank.A, calcular(ClubRank.A, 15, 16, false, true));
        assertEquals(ClubRank.S, calcular(ClubRank.A, 1, 16, false, true));
        assertEquals(ClubRank.S, calcular(ClubRank.S, 10, 16, false, true));
    }

    private static ClubRank calcular(
            ClubRank inicial, int posicao, int total, boolean campeao, boolean protegido) {
        CampeonatoClube snap = new CampeonatoClube();
        snap.setRank(inicial);
        return RankEvolutionPolicy.calcularNovoRank(snap, posicao, total, campeao, protegido);
    }
}
