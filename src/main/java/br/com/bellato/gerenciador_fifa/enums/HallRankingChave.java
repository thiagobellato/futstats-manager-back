package br.com.bellato.gerenciador_fifa.enums;

/**
 * Chaves dos rankings pagináveis do Hall da Fama.
 */
public enum HallRankingChave {
    clubesMaisTitulos,
    clubesMaisVices,
    clubesMaisFinais,
    clubesMaiorSequenciaTitulos,
    clubesMaisParticipacoes,
    clubesMelhorAproveitamento,
    jogadoresMaisGols,
    jogadoresMaisAssistencias,
    jogadoresMaisGolsContra,
    jogadoresMaisAmarelos,
    jogadoresMaisVermelhos,
    jogadoresMaisTitulos;

    public static HallRankingChave from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Chave de ranking inválida.");
        }
        try {
            return HallRankingChave.valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Chave de ranking inválida: " + value);
        }
    }
}
