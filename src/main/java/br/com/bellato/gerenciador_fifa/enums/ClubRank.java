package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum ClubRank {
    S("Rank S", "S"),
    A("Rank A", "A"),
    B("Rank B", "B"),
    C("Rank C", "C"),
    D("Rank D", "D"),
    E("Rank E", "E");

    private final String descricao;
    private final String sigla;

    ClubRank(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getDatabaseValue() {
        return descricao + "/" + sigla;
    }

    @JsonValue
    public String toJson() {
        return sigla;
    }

    @JsonCreator
    public static ClubRank fromJson(String value) {
        for (ClubRank rank : values()) {
            if (rank.sigla.equalsIgnoreCase(value) || rank.descricao.equalsIgnoreCase(value)) {
                return rank;
            }
        }
        throw new IllegalArgumentException("Rank de clube inválido: " + value);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSigla() {
        return sigla;
    }

    /**
     * Nível hierárquico: S=0 (melhor) … E=5 (pior).
     */
    public int getNivel() {
        return ordinal();
    }

    public static ClubRank fromNivel(int nivel) {
        ClubRank[] values = values();
        int idx = Math.max(0, Math.min(values.length - 1, nivel));
        return values[idx];
    }

    /** Promove em direção a S (melhora o rank). */
    public ClubRank promover(int niveis) {
        if (niveis <= 0) {
            return this;
        }
        return fromNivel(getNivel() - niveis);
    }

    /** Rebaixa em direção a E (piora o rank). */
    public ClubRank rebaixar(int niveis) {
        if (niveis <= 0) {
            return this;
        }
        return fromNivel(getNivel() + niveis);
    }

    @Converter(autoApply = false)
    public static class ClubRankConverter implements AttributeConverter<ClubRank, String> {

        @Override
        public String convertToDatabaseColumn(ClubRank attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getDatabaseValue();
        }

        @Override
        public ClubRank convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            for (ClubRank rank : ClubRank.values()) {
                if (dbData.equals(rank.getDatabaseValue())) {
                    return rank;
                }
            }
            throw new IllegalArgumentException("Rank de clube inválido no banco de dados: " + dbData);
        }
    }
}
