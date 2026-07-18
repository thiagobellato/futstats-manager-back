package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum StatusPartida {
    AGUARDANDO_RESULTADO("Aguardando Resultado", "AGUARDANDO_RESULTADO"),
    FINALIZADA("Finalizada", "FINALIZADA");

    private final String descricao;
    private final String codigo;

    StatusPartida(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDatabaseValue() {
        return descricao + "/" + codigo;
    }

    @JsonValue
    public String toJson() {
        return codigo;
    }

    @JsonCreator
    public static StatusPartida fromJson(String value) {
        for (StatusPartida status : values()) {
            if (status.codigo.equalsIgnoreCase(value) || status.descricao.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de partida inválido: " + value);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Converter(autoApply = false)
    public static class StatusPartidaConverter implements AttributeConverter<StatusPartida, String> {

        @Override
        public String convertToDatabaseColumn(StatusPartida attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getDatabaseValue();
        }

        @Override
        public StatusPartida convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            for (StatusPartida status : StatusPartida.values()) {
                if (dbData.equals(status.getDatabaseValue())) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Status de partida inválido no banco de dados: " + dbData);
        }
    }
}
