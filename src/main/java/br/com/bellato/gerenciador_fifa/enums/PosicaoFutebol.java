package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum PosicaoFutebol {
    GOLEIRO("Goleiro", "GOL"),
    LATERAL_DIREITO("Lateral Direito", "LD"),
    ALA_DIREITO("Ala Direito", "ADD"),
    ZAGUEIRO_DIREITO("Zagueiro Direito", "ZAD"),
    ZAGUEIRO_CENTRAL("Zagueiro Central", "ZAG"),
    ZAGUEIRO_ESQUERDO("Zagueiro Esquerdo", "ZAE"),
    LATERAL_ESQUERDO("Lateral Esquerdo", "LE"),
    ALA_ESQUERDO("Ala Esquerdo", "ADE"),
    VOLANTE("Volante", "VOL"),
    MEIA_CENTRAL("Meia Central", "MC"),
    MEIA_DIREITA("Meia Direita", "MD"),
    MEIA_ESQUERDA("Meia Esquerda", "ME"),
    MEIA_OFENSIVO("Meia Ofensivo", "MEI"),
    PONTA_DIREITA("Ponta Direita", "PD"),
    PONTA_ESQUERDA("Ponta Esquerda", "PE"),
    SEGUNDO_ATACANTE("Segundo Atacante", "SA"),
    ATACANTE("Atacante", "ATA");

    private String descricaoPosicao;
    private String siglaPosicao;

    PosicaoFutebol(String descricaoPosicao, String siglaPosicao) {
        this.descricaoPosicao = descricaoPosicao;
        this.siglaPosicao = siglaPosicao;
    }

    // Método utilizado pelo JPA para salvar no banco de dados
    public String getDatabaseValue() {
        return descricaoPosicao + "/" + siglaPosicao;
    }

    @JsonValue
    public String toJson() {
        return siglaPosicao; // Aqui você pode escolher entre `siglaPosicao` ou `descricaoPosicao`
    }

    @JsonCreator
    public static PosicaoFutebol fromJson(String value) {
        for (PosicaoFutebol posicao : values()) {
            if (posicao.siglaPosicao.equalsIgnoreCase(value) || posicao.descricaoPosicao.equalsIgnoreCase(value)) {
                return posicao;
            }
        }
        throw new IllegalArgumentException("Posição inválida: " + value);
    }

    public String getDescricaoPosicao() {
        return descricaoPosicao;
    }

    public String getSiglaPosicao() {
        return siglaPosicao;
    }

    @Converter(autoApply = true)
    public static class PosicaoFutebolConverter implements AttributeConverter<PosicaoFutebol, String> {

        @Override
        public String convertToDatabaseColumn(PosicaoFutebol attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getDatabaseValue();
        }

        @Override
        public PosicaoFutebol convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            for (PosicaoFutebol posicao : PosicaoFutebol.values()) {
                if (dbData.equals(posicao.getDatabaseValue())) {
                    return posicao;
                }
            }
            throw new IllegalArgumentException("Posição inválida no banco de dados: " + dbData);
        }
    }
}
