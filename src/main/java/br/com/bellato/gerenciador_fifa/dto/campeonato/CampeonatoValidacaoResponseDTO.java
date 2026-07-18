package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoValidacaoResponseDTO {

    private boolean valido;
    private String mensagem;
    private Long quantidadeAtletasEstimada;

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getQuantidadeAtletasEstimada() {
        return quantidadeAtletasEstimada;
    }

    public void setQuantidadeAtletasEstimada(Long quantidadeAtletasEstimada) {
        this.quantidadeAtletasEstimada = quantidadeAtletasEstimada;
    }
}
