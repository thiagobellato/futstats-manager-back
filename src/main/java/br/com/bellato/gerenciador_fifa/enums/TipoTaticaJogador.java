package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum TipoTaticaJogador {
    TITULAR("Titular"),
    RESERVA("Reserva");

    private final String descricao;

    TipoTaticaJogador(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static TipoTaticaJogador fromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        for (TipoTaticaJogador tipo : values()) {
            if (tipo.name().equalsIgnoreCase(trimmed)
                    || tipo.descricao.equalsIgnoreCase(trimmed)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de jogador tático inválido: " + value);
    }

    @Converter(autoApply = false)
    public static class TipoTaticaJogadorConverter implements AttributeConverter<TipoTaticaJogador, String> {

        @Override
        public String convertToDatabaseColumn(TipoTaticaJogador attribute) {
            return attribute != null ? attribute.name() : null;
        }

        @Override
        public TipoTaticaJogador convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return null;
            }
            return TipoTaticaJogador.fromJson(dbData);
        }
    }
}
