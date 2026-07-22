package br.com.bellato.gerenciador_fifa.dto.estatistica_atleta;

import com.fasterxml.jackson.annotation.JsonInclude;

public class EstatisticaAtletaRequestDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer gols;
    private Integer assistencias;
    private Integer cartaoAmarelo;
    private Integer cartaoVermelho;
    private Integer golsContra;
    private Long atletaId;
    private Long ClubeId;

    public Integer getGols() {
        return gols;
    }

    public void setGols(Integer gols) {
        this.gols = gols;
    }

    public Integer getAssistencias() {
        return assistencias;
    }

    public void setAssistencias(Integer assistencias) {
        this.assistencias = assistencias;
    }

    public Integer getCartaoAmarelo() {
        return cartaoAmarelo;
    }

    public void setCartaoAmarelo(Integer cartaoAmarelo) {
        this.cartaoAmarelo = cartaoAmarelo;
    }

    public Integer getCartaoVermelho() {
        return cartaoVermelho;
    }

    public void setCartaoVermelho(Integer cartaoVermelho) {
        this.cartaoVermelho = cartaoVermelho;
    }

    public Integer getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(Integer golsContra) {
        this.golsContra = golsContra;
    }

    public Long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
    }

    public Long getClubeId() {
        return ClubeId;
    }

    public void setClubeId(Long clubeId) {
        ClubeId = clubeId;
    }

}
