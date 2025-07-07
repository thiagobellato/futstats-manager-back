package br.com.bellato.gerenciador_fifa.dto.posicao;

public class PosicaoDTO {
    private String sigla;
    private String descricao;

    public PosicaoDTO(String sigla, String descricao) {
        this.sigla = sigla;
        this.descricao = descricao;
    }

    public String getSigla() {
        return sigla;
    }

    public String getDescricao() {
        return descricao;
    }
}
