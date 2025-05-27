package br.com.bellato.gerenciador_fifa.config;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PosicaoFutebolConverter implements AttributeConverter<PosicaoFutebol, String> {

    @Override
    public String convertToDatabaseColumn(PosicaoFutebol attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDatabaseValue(); // Salva o valor concatenado no banco de dados
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


