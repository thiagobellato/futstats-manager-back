package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum FormacaoTatica {
    F_4_4_2("4-4-2"),
    F_4_3_3("4-3-3"),
    F_4_2_3_1("4-2-3-1"),
    F_4_1_2_1_2("4-1-2-1-2"),
    F_4_3_2_1("4-3-2-1"),
    F_3_5_2("3-5-2"),
    F_3_4_3("3-4-3"),
    F_5_3_2("5-3-2"),
    F_5_2_3("5-2-3");

    private final String label;

    FormacaoTatica(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static FormacaoTatica fromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        for (FormacaoTatica formacao : values()) {
            if (formacao.name().equalsIgnoreCase(trimmed)
                    || formacao.label.equalsIgnoreCase(trimmed)) {
                return formacao;
            }
        }
        throw new IllegalArgumentException("Formação tática inválida: " + value);
    }

    @Converter(autoApply = false)
    public static class FormacaoTaticaConverter implements AttributeConverter<FormacaoTatica, String> {

        @Override
        public String convertToDatabaseColumn(FormacaoTatica attribute) {
            return attribute != null ? attribute.getLabel() : null;
        }

        @Override
        public FormacaoTatica convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return null;
            }
            return FormacaoTatica.fromJson(dbData);
        }
    }
}
