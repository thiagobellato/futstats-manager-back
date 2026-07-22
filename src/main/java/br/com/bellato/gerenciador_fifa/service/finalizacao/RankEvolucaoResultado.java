package br.com.bellato.gerenciador_fifa.service.finalizacao;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

/**
 * Resultado da política de ranks (somente leitura / preview / persistência).
 */
public record RankEvolucaoResultado(
        Long campeonatoClubeId,
        ClubRank rankInicial,
        ClubRank rankFinal) {

    public String movimento() {
        if (rankInicial == null || rankFinal == null) {
            return "PERMANECEU";
        }
        int diff = rankFinal.getNivel() - rankInicial.getNivel();
        if (diff < 0) {
            return "PROMOCAO";
        }
        if (diff > 0) {
            return "REBAIXAMENTO";
        }
        return "PERMANECEU";
    }
}
