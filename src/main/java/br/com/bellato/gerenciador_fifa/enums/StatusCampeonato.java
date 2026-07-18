package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum StatusCampeonato {
    EM_CONFIGURACAO("Em Configuração", "EM_CONFIGURACAO"),
    AGUARDANDO_INICIO("Aguardando Início", "AGUARDANDO_INICIO"),
    EM_ANDAMENTO("Em Andamento", "EM_ANDAMENTO"),
    FINALIZADO("Finalizado", "FINALIZADO");

    private final String descricao;
    private final String codigo;

    StatusCampeonato(String descricao, String codigo) {
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
    public static StatusCampeonato fromJson(String value) {
        for (StatusCampeonato status : values()) {
            if (status.codigo.equalsIgnoreCase(value) || status.descricao.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de campeonato inválido: " + value);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Converter(autoApply = false)
    public static class StatusCampeonatoConverter implements AttributeConverter<StatusCampeonato, String> {

        @Override
        public String convertToDatabaseColumn(StatusCampeonato attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getDatabaseValue();
        }

        @Override
        public StatusCampeonato convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            for (StatusCampeonato status : StatusCampeonato.values()) {
                if (dbData.equals(status.getDatabaseValue())) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Status de campeonato inválido no banco de dados: " + dbData);
        }
    }
}
