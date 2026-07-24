package br.com.bellato.gerenciador_fifa.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Estatísticas agregadas por usuário (campeonatos autenticados).
 * Atualizadas incrementalmente na finalização — nunca recalculadas do zero.
 * Espelha o papel de {@link EstatisticaClube} / {@link EstatisticaAtleta}.
 */
@Entity
@Table(name = "estatisticas_usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_estatisticas_usuario_user", columnNames = { "user_id" })
})
public class EstatisticaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userStatisticsId")
    private Long estatisticaUsuarioId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @Column(name = "championshipsPlayed", nullable = false)
    private int campeonatosDisputados;

    @Column(name = "championshipsWon", nullable = false)
    private int campeonatosVencidos;

    @Column(name = "runnerUps", nullable = false)
    private int viceCampeonatos;

    @Column(name = "wins", nullable = false)
    private int vitorias;

    @Column(name = "draws", nullable = false)
    private int empates;

    @Column(name = "losses", nullable = false)
    private int derrotas;

    @Column(name = "goalsFor", nullable = false)
    private int golsMarcados;

    @Column(name = "goalsAgainst", nullable = false)
    private int golsSofridos;

    @Column(name = "goalDifference", nullable = false)
    private int saldoGols;

    @Column(name = "clubsUsed", nullable = false)
    private int clubesUsados;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_clube_id")
    private Clube clubeFavorito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "most_titles_clube_id")
    private Clube clubeMaisTitulos;

    @Column(name = "longestWinningStreak", nullable = false)
    private int maiorSequenciaVitorias;

    @Column(name = "longestUnbeatenStreak", nullable = false)
    private int maiorSequenciaInvicta;

    @Column(name = "currentWinningStreak", nullable = false)
    private int sequenciaVitoriasAtual;

    @Column(name = "currentUnbeatenStreak", nullable = false)
    private int sequenciaInvictaAtual;

    @Column(name = "lastChampionshipDate")
    private LocalDateTime dataUltimoCampeonato;

    public EstatisticaUsuario() {
    }

    public Long getEstatisticaUsuarioId() {
        return estatisticaUsuarioId;
    }

    public void setEstatisticaUsuarioId(Long estatisticaUsuarioId) {
        this.estatisticaUsuarioId = estatisticaUsuarioId;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
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

    public int getPartidasDisputadas() {
        return vitorias + empates + derrotas;
    }

    public int getClubesUsados() {
        return clubesUsados;
    }

    public void setClubesUsados(int clubesUsados) {
        this.clubesUsados = clubesUsados;
    }

    public Clube getClubeFavorito() {
        return clubeFavorito;
    }

    public void setClubeFavorito(Clube clubeFavorito) {
        this.clubeFavorito = clubeFavorito;
    }

    public Clube getClubeMaisTitulos() {
        return clubeMaisTitulos;
    }

    public void setClubeMaisTitulos(Clube clubeMaisTitulos) {
        this.clubeMaisTitulos = clubeMaisTitulos;
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

    public int getSequenciaVitoriasAtual() {
        return sequenciaVitoriasAtual;
    }

    public void setSequenciaVitoriasAtual(int sequenciaVitoriasAtual) {
        this.sequenciaVitoriasAtual = sequenciaVitoriasAtual;
    }

    public int getSequenciaInvictaAtual() {
        return sequenciaInvictaAtual;
    }

    public void setSequenciaInvictaAtual(int sequenciaInvictaAtual) {
        this.sequenciaInvictaAtual = sequenciaInvictaAtual;
    }

    public LocalDateTime getDataUltimoCampeonato() {
        return dataUltimoCampeonato;
    }

    public void setDataUltimoCampeonato(LocalDateTime dataUltimoCampeonato) {
        this.dataUltimoCampeonato = dataUltimoCampeonato;
    }
}
