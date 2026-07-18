package br.com.bellato.gerenciador_fifa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum StatusCampeonato {
    EM_CONFIGURACAO("Em Configuração", "EM_CONFIGURACAO"),
    AGUARDANDO_INICIO("Aguardando Início", "AGUARDANDO_INICIO"),
    PRONTO_PARA_SORTEIO("Pronto para Sorteio", "PRONTO_PARA_SORTEIO"),
    PRIMEIRA_RODADA("Primeira Rodada", "PRIMEIRA_RODADA"),
    RODADA_EM_ANDAMENTO("Rodada em Andamento", "RODADA_EM_ANDAMENTO"),
    RODADA_FINALIZADA("Rodada Finalizada", "RODADA_FINALIZADA"),
    AGUARDANDO_REMANEJAMENTO("Aguardando Remanejamento", "AGUARDANDO_REMANEJAMENTO"),
    GERANDO_PROXIMA_RODADA("Gerando Próxima Rodada", "GERANDO_PROXIMA_RODADA"),
    AGUARDANDO_ESCOLHA_DO_CAMPEAO("Aguardando Definição do Clube Campeão", "AGUARDANDO_ESCOLHA_DO_CAMPEAO"),
    AGUARDANDO_FINALIZACAO("Aguardando Finalização", "AGUARDANDO_FINALIZACAO"),
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

    public boolean isAtivo() {
        return this != EM_CONFIGURACAO
                && this != AGUARDANDO_ESCOLHA_DO_CAMPEAO
                && this != AGUARDANDO_FINALIZACAO
                && this != FINALIZADO;
    }

    public boolean possuiCampeaoDefinido() {
        return this == AGUARDANDO_FINALIZACAO || this == FINALIZADO;
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
