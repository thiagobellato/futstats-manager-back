package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum TipoEventoPartida {
    GOL("Gol", "GOL"),
    GOL_CONTRA("Gol Contra", "GOL_CONTRA"),
    ASSISTENCIA("Assistência", "ASSISTENCIA"),
    CARTAO_AMARELO("Cartão Amarelo", "CARTAO_AMARELO"),
    CARTAO_VERMELHO("Cartão Vermelho", "CARTAO_VERMELHO");

    private final String descricao;
    private final String codigo;

    TipoEventoPartida(String descricao, String codigo) {
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
    public static TipoEventoPartida fromJson(String value) {
        for (TipoEventoPartida tipo : values()) {
            if (tipo.codigo.equalsIgnoreCase(value) || tipo.descricao.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de evento inválido: " + value);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isGolQueContaNoPlacar() {
        return this == GOL || this == GOL_CONTRA;
    }

    @Converter(autoApply = false)
    public static class TipoEventoPartidaConverter implements AttributeConverter<TipoEventoPartida, String> {

        @Override
        public String convertToDatabaseColumn(TipoEventoPartida attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getDatabaseValue();
        }

        @Override
        public TipoEventoPartida convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            for (TipoEventoPartida tipo : TipoEventoPartida.values()) {
                if (dbData.equals(tipo.getDatabaseValue())) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Tipo de evento inválido no banco de dados: " + dbData);
        }
    }
}
