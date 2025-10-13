package br.com.bellato.gerenciador_fifa.dto.estatistica_atleta;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstatisticaAtletaResponseDTO {

    private Long estatisticaAtletaId;
    private String nomeAtleta;
    private String nomeClube;
    private Integer gols;
    private Integer assistencias;
    private Integer cartaoAmarelo;
    private Integer cartaoVermelho;
    private Long atletaId;

    public Long getEstatisticaAtletaId() {
        return estatisticaAtletaId;
    }

    public void setEstatisticaAtletaId(Long estatisticaAtletaId) {
        this.estatisticaAtletaId = estatisticaAtletaId;
    }

    public String getNomeAtleta() {
        return nomeAtleta;
    }

    public void setNomeAtleta(String nomeAtleta) {
        this.nomeAtleta = nomeAtleta;
    }

    public String getNomeClube() {
        return nomeClube;
    }

    public void setNomeClube(String nomeClube) {
        this.nomeClube = nomeClube;
    }

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

    public Long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
    }

}
