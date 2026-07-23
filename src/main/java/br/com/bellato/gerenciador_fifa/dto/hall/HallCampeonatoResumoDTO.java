package br.com.bellato.gerenciador_fifa.dto.hall;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallCampeonatoResumoDTO {

    private Long campeonatoId;
    private String nome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataFinalizacao;
    private Integer quantidadeClubes;
    private Integer quantidadeCompetidores;
    private String competidor1Nome;
    private String competidor2Nome;
    private String campeaoClubeNome;
    private Long campeaoClubeId;
    private String campeaoCompetidorNome;
    private Integer campeaoCompetidor;
    private String viceCampeaoClubeNome;
    private Long viceCampeaoClubeId;
    private Integer quantidadePartidas;
    private Integer quantidadeGols;
    private Integer quantidadeCartoes;
    private Long duracaoMinutos;
    private String duracaoFormatada;

    public Long getCampeonatoId() {
        return campeonatoId;
    }

    public void setCampeonatoId(Long campeonatoId) {
        this.campeonatoId = campeonatoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public Integer getQuantidadeClubes() {
        return quantidadeClubes;
    }

    public void setQuantidadeClubes(Integer quantidadeClubes) {
        this.quantidadeClubes = quantidadeClubes;
    }

    public Integer getQuantidadeCompetidores() {
        return quantidadeCompetidores;
    }

    public void setQuantidadeCompetidores(Integer quantidadeCompetidores) {
        this.quantidadeCompetidores = quantidadeCompetidores;
    }

    public String getCompetidor1Nome() {
        return competidor1Nome;
    }

    public void setCompetidor1Nome(String competidor1Nome) {
        this.competidor1Nome = competidor1Nome;
    }

    public String getCompetidor2Nome() {
        return competidor2Nome;
    }

    public void setCompetidor2Nome(String competidor2Nome) {
        this.competidor2Nome = competidor2Nome;
    }

    public String getCampeaoClubeNome() {
        return campeaoClubeNome;
    }

    public void setCampeaoClubeNome(String campeaoClubeNome) {
        this.campeaoClubeNome = campeaoClubeNome;
    }

    public Long getCampeaoClubeId() {
        return campeaoClubeId;
    }

    public void setCampeaoClubeId(Long campeaoClubeId) {
        this.campeaoClubeId = campeaoClubeId;
    }

    public String getCampeaoCompetidorNome() {
        return campeaoCompetidorNome;
    }

    public void setCampeaoCompetidorNome(String campeaoCompetidorNome) {
        this.campeaoCompetidorNome = campeaoCompetidorNome;
    }

    public Integer getCampeaoCompetidor() {
        return campeaoCompetidor;
    }

    public void setCampeaoCompetidor(Integer campeaoCompetidor) {
        this.campeaoCompetidor = campeaoCompetidor;
    }

    public String getViceCampeaoClubeNome() {
        return viceCampeaoClubeNome;
    }

    public void setViceCampeaoClubeNome(String viceCampeaoClubeNome) {
        this.viceCampeaoClubeNome = viceCampeaoClubeNome;
    }

    public Long getViceCampeaoClubeId() {
        return viceCampeaoClubeId;
    }

    public void setViceCampeaoClubeId(Long viceCampeaoClubeId) {
        this.viceCampeaoClubeId = viceCampeaoClubeId;
    }

    public Integer getQuantidadePartidas() {
        return quantidadePartidas;
    }

    public void setQuantidadePartidas(Integer quantidadePartidas) {
        this.quantidadePartidas = quantidadePartidas;
    }

    public Integer getQuantidadeGols() {
        return quantidadeGols;
    }

    public void setQuantidadeGols(Integer quantidadeGols) {
        this.quantidadeGols = quantidadeGols;
    }

    public Integer getQuantidadeCartoes() {
        return quantidadeCartoes;
    }

    public void setQuantidadeCartoes(Integer quantidadeCartoes) {
        this.quantidadeCartoes = quantidadeCartoes;
    }

    public Long getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Long duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getDuracaoFormatada() {
        return duracaoFormatada;
    }

    public void setDuracaoFormatada(String duracaoFormatada) {
        this.duracaoFormatada = duracaoFormatada;
    }
}
