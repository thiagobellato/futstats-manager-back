package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.StatusRodada;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoRodadaResponseDTO {

    private Long campeonatoRodadaId;
    private Integer numeroRodada;
    private String nome;
    private StatusRodada status;
    private Boolean faseAssincrona;
    private Integer competidorRemanejamento;
    private List<CampeonatoPartidaResponseDTO> partidas;
    private Integer quantidadePartidas;
    private Integer partidasFinalizadas;

    public Long getCampeonatoRodadaId() {
        return campeonatoRodadaId;
    }

    public void setCampeonatoRodadaId(Long campeonatoRodadaId) {
        this.campeonatoRodadaId = campeonatoRodadaId;
    }

    public Integer getNumeroRodada() {
        return numeroRodada;
    }

    public void setNumeroRodada(Integer numeroRodada) {
        this.numeroRodada = numeroRodada;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public StatusRodada getStatus() {
        return status;
    }

    public void setStatus(StatusRodada status) {
        this.status = status;
    }

    public Boolean getFaseAssincrona() {
        return faseAssincrona;
    }

    public void setFaseAssincrona(Boolean faseAssincrona) {
        this.faseAssincrona = faseAssincrona;
    }

    public Integer getCompetidorRemanejamento() {
        return competidorRemanejamento;
    }

    public void setCompetidorRemanejamento(Integer competidorRemanejamento) {
        this.competidorRemanejamento = competidorRemanejamento;
    }

    public List<CampeonatoPartidaResponseDTO> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<CampeonatoPartidaResponseDTO> partidas) {
        this.partidas = partidas;
    }

    public Integer getQuantidadePartidas() {
        return quantidadePartidas;
    }

    public void setQuantidadePartidas(Integer quantidadePartidas) {
        this.quantidadePartidas = quantidadePartidas;
    }

    public Integer getPartidasFinalizadas() {
        return partidasFinalizadas;
    }

    public void setPartidasFinalizadas(Integer partidasFinalizadas) {
        this.partidasFinalizadas = partidasFinalizadas;
    }
}
