package br.com.bellato.gerenciador_fifa.dto.user;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RivalidadeItemDTO {

    private Long rivalidadeId;
    private Long rivalUserId;
    private String rivalUsername;
    private String rivalDisplayName;
    private int confrontos;
    private int vitorias;
    private int derrotas;
    private int empates;
    private int golsMarcados;
    private int golsSofridos;
    private int saldoGols;
    private Double aproveitamento;
    private int campeonatosDisputados;
    private LocalDateTime dataUltimoConfronto;
    private Long ultimoVencedorUserId;
    private String ultimoVencedorUsername;

    public Long getRivalidadeId() {
        return rivalidadeId;
    }

    public void setRivalidadeId(Long rivalidadeId) {
        this.rivalidadeId = rivalidadeId;
    }

    public Long getRivalUserId() {
        return rivalUserId;
    }

    public void setRivalUserId(Long rivalUserId) {
        this.rivalUserId = rivalUserId;
    }

    public String getRivalUsername() {
        return rivalUsername;
    }

    public void setRivalUsername(String rivalUsername) {
        this.rivalUsername = rivalUsername;
    }

    public String getRivalDisplayName() {
        return rivalDisplayName;
    }

    public void setRivalDisplayName(String rivalDisplayName) {
        this.rivalDisplayName = rivalDisplayName;
    }

    public int getConfrontos() {
        return confrontos;
    }

    public void setConfrontos(int confrontos) {
        this.confrontos = confrontos;
    }

    public int getVitorias() {
        return vitorias;
    }

    public void setVitorias(int vitorias) {
        this.vitorias = vitorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }

    public int getEmpates() {
        return empates;
    }

    public void setEmpates(int empates) {
        this.empates = empates;
    }

    public int getGolsMarcados() {
        return golsMarcados;
    }

    public void setGolsMarcados(int golsMarcados) {
        this.golsMarcados = golsMarcados;
    }

    public int getGolsSofridos() {
        return golsSofridos;
    }

    public void setGolsSofridos(int golsSofridos) {
        this.golsSofridos = golsSofridos;
    }

    public int getSaldoGols() {
        return saldoGols;
    }

    public void setSaldoGols(int saldoGols) {
        this.saldoGols = saldoGols;
    }

    public Double getAproveitamento() {
        return aproveitamento;
    }

    public void setAproveitamento(Double aproveitamento) {
        this.aproveitamento = aproveitamento;
    }

    public int getCampeonatosDisputados() {
        return campeonatosDisputados;
    }

    public void setCampeonatosDisputados(int campeonatosDisputados) {
        this.campeonatosDisputados = campeonatosDisputados;
    }

    public LocalDateTime getDataUltimoConfronto() {
        return dataUltimoConfronto;
    }

    public void setDataUltimoConfronto(LocalDateTime dataUltimoConfronto) {
        this.dataUltimoConfronto = dataUltimoConfronto;
    }

    public Long getUltimoVencedorUserId() {
        return ultimoVencedorUserId;
    }

    public void setUltimoVencedorUserId(Long ultimoVencedorUserId) {
        this.ultimoVencedorUserId = ultimoVencedorUserId;
    }

    public String getUltimoVencedorUsername() {
        return ultimoVencedorUsername;
    }

    public void setUltimoVencedorUsername(String ultimoVencedorUsername) {
        this.ultimoVencedorUsername = ultimoVencedorUsername;
    }
}
