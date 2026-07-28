package br.com.bellato.gerenciador_fifa.enums;

/**
 * Motivo da suspensão disciplinar no campeonato.
 * Persistido como código na tabela campeonato_suspensao.
 */
public enum MotivoSuspensao {
    CARTAO_VERMELHO("CARTAO_VERMELHO", "Suspenso por cartão vermelho", "VERMELHO"),
    SEGUNDO_AMARELO("SEGUNDO_AMARELO", "Suspenso por segundo amarelo", "VERMELHO"),
    ACUMULO_AMARELOS("ACUMULO_AMARELOS", "Suspenso por acúmulo de amarelos", "ACUMULO_AMARELOS");

    private final String codigo;
    private final String descricao;
    private final String tipoUi;

    MotivoSuspensao(String codigo, String descricao, String tipoUi) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoUi = tipoUi;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Agrupa vermelho direto / segundo amarelo vs acúmulo para a UI. */
    public String getTipoUi() {
        return tipoUi;
    }

    public static MotivoSuspensao fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return CARTAO_VERMELHO;
        }
        for (MotivoSuspensao m : values()) {
            if (m.codigo.equalsIgnoreCase(codigo) || m.name().equalsIgnoreCase(codigo)) {
                return m;
            }
        }
        // Compatibilidade com registros antigos sem motivo tipado
        String lower = codigo.toLowerCase();
        if (lower.contains("acúmulo") || lower.contains("acumulo")) {
            return ACUMULO_AMARELOS;
        }
        if (lower.contains("segundo")) {
            return SEGUNDO_AMARELO;
        }
        return CARTAO_VERMELHO;
    }
}
