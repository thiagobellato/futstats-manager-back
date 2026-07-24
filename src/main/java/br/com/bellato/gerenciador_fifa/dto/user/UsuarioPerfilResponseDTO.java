package br.com.bellato.gerenciador_fifa.dto.user;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioPerfilResponseDTO {

    private Long id;
    private String username;
    private String displayName;
    private LocalDateTime createdAt;

    private int campeonatosDisputados;
    private int campeonatosVencidos;
    private int viceCampeonatos;
    private int vitorias;
    private int empates;
    private int derrotas;
    private int partidasDisputadas;
    private int golsMarcados;
    private int golsSofridos;
    private int saldoGols;
    private Double aproveitamento;
    private Double mediaGols;
    private int maiorSequenciaVitorias;
    private int maiorSequenciaInvicta;
    private Integer clubesUsados;
    private String clubeFavorito;
    private Long clubeFavoritoId;
    private String clubeMaisTitulos;
    private Long clubeMaisTitulosId;
    private LocalDateTime dataUltimoCampeonato;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getCampeonatosDisputados() {
        return campeonatosDisputados;
    }

    public void setCampeonatosDisputados(int campeonatosDisputados) {
        this.campeonatosDisputados = campeonatosDisputados;
    }

    public int getCampeonatosVencidos() {
        return campeonatosVencidos;
    }

    public void setCampeonatosVencidos(int campeonatosVencidos) {
        this.campeonatosVencidos = campeonatosVencidos;
    }

    public int getViceCampeonatos() {
        return viceCampeonatos;
    }

    public void setViceCampeonatos(int viceCampeonatos) {
        this.viceCampeonatos = viceCampeonatos;
    }

    public int getVitorias() {
        return vitorias;
    }

    public void setVitorias(int vitorias) {
        this.vitorias = vitorias;
    }

    public int getEmpates() {
        return empates;
    }

    public void setEmpates(int empates) {
        this.empates = empates;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }

    public int getPartidasDisputadas() {
        return partidasDisputadas;
    }

    public void setPartidasDisputadas(int partidasDisputadas) {
        this.partidasDisputadas = partidasDisputadas;
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

    public Double getMediaGols() {
        return mediaGols;
    }

    public void setMediaGols(Double mediaGols) {
        this.mediaGols = mediaGols;
    }

    public int getMaiorSequenciaVitorias() {
        return maiorSequenciaVitorias;
    }

    public void setMaiorSequenciaVitorias(int maiorSequenciaVitorias) {
        this.maiorSequenciaVitorias = maiorSequenciaVitorias;
    }

    public int getMaiorSequenciaInvicta() {
        return maiorSequenciaInvicta;
    }

    public void setMaiorSequenciaInvicta(int maiorSequenciaInvicta) {
        this.maiorSequenciaInvicta = maiorSequenciaInvicta;
    }

    public Integer getClubesUsados() {
        return clubesUsados;
    }

    public void setClubesUsados(Integer clubesUsados) {
        this.clubesUsados = clubesUsados;
    }

    public String getClubeFavorito() {
        return clubeFavorito;
    }

    public void setClubeFavorito(String clubeFavorito) {
        this.clubeFavorito = clubeFavorito;
    }

    public Long getClubeFavoritoId() {
        return clubeFavoritoId;
    }

    public void setClubeFavoritoId(Long clubeFavoritoId) {
        this.clubeFavoritoId = clubeFavoritoId;
    }

    public String getClubeMaisTitulos() {
        return clubeMaisTitulos;
    }

    public void setClubeMaisTitulos(String clubeMaisTitulos) {
        this.clubeMaisTitulos = clubeMaisTitulos;
    }

    public Long getClubeMaisTitulosId() {
        return clubeMaisTitulosId;
    }

    public void setClubeMaisTitulosId(Long clubeMaisTitulosId) {
        this.clubeMaisTitulosId = clubeMaisTitulosId;
    }

    public LocalDateTime getDataUltimoCampeonato() {
        return dataUltimoCampeonato;
    }

    public void setDataUltimoCampeonato(LocalDateTime dataUltimoCampeonato) {
        this.dataUltimoCampeonato = dataUltimoCampeonato;
    }
}
