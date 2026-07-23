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

    /**
     * Valor canônico persistido no banco: S, A, B, C, D ou E.
     */
    public String getDatabaseValue() {
        return sigla;
    }

    @JsonValue
    public String toJson() {
        return sigla;
    }

    @JsonCreator
    public static ClubRank fromJson(String value) {
        ClubRank rank = parse(value);
        if (rank == null) {
            throw new IllegalArgumentException("Rank de clube inválido: " + value);
        }
        return rank;
    }

    /**
     * Aceita formatos legados e canônicos: S, Rank S, Rank S/S, RANK_S, etc.
     */
    public static ClubRank parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String raw = value.trim();
        String normalized = raw.toUpperCase()
                .replace("RANK_", "")
                .replace("RANK ", "")
                .replace(" ", "");

        // "S/S", "RANKS/S" após limpeza parcial → pega o último token de 1 letra
        if (normalized.contains("/")) {
            String[] parts = normalized.split("/");
            normalized = parts[parts.length - 1];
        }

        for (ClubRank rank : values()) {
            if (rank.sigla.equalsIgnoreCase(raw)
                    || rank.sigla.equalsIgnoreCase(normalized)
                    || rank.descricao.equalsIgnoreCase(raw)
                    || rank.name().equalsIgnoreCase(raw)
                    || rank.name().equalsIgnoreCase(normalized)
                    || rank.getDatabaseValue().equalsIgnoreCase(normalized)) {
                return rank;
            }
        }

        // Última tentativa: primeira letra A–E / S
        if (normalized.length() == 1) {
            for (ClubRank rank : values()) {
                if (rank.sigla.equalsIgnoreCase(normalized)) {
                    return rank;
                }
            }
        }
        return null;
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
            ClubRank rank = ClubRank.parse(dbData);
            if (rank == null) {
                throw new IllegalArgumentException("Rank de clube inválido no banco de dados: " + dbData);
            }
            return rank;
        }
    }
}
