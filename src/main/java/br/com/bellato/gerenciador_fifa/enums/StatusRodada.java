package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum StatusRodada {
    AGUARDANDO_REMANEJAMENTO("Aguardando Remanejamento", "AGUARDANDO_REMANEJAMENTO"),
    EM_ANDAMENTO("Em Andamento", "EM_ANDAMENTO"),
    FINALIZADA("Finalizada", "FINALIZADA");

    private final String descricao;
    private final String codigo;

    StatusRodada(String descricao, String codigo) {
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
    public static StatusRodada fromJson(String value) {
        for (StatusRodada status : values()) {
            if (status.codigo.equalsIgnoreCase(value) || status.descricao.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de rodada inválido: " + value);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Converter(autoApply = false)
    public static class StatusRodadaConverter implements AttributeConverter<StatusRodada, String> {

        @Override
        public String convertToDatabaseColumn(StatusRodada attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getDatabaseValue();
        }

        @Override
        public StatusRodada convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            for (StatusRodada status : StatusRodada.values()) {
                if (dbData.equals(status.getDatabaseValue())) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Status de rodada inválido no banco de dados: " + dbData);
        }
    }
}
