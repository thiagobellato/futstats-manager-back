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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Rivalidade agregada entre dois usuários (par canônico usuarioA.id &lt; usuarioB.id).
 * Atualizada incrementalmente após cada campeonato autenticado.
 */
@Entity
@Table(name = "rivalidade_usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_rivalidade_usuario_par", columnNames = { "user_a_id", "user_b_id" })
})
public class RivalidadeUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userRivalryId")
    private Long rivalidadeUsuarioId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_a_id", nullable = false)
    private User usuarioA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_b_id", nullable = false)
    private User usuarioB;

    @Column(name = "matches", nullable = false)
    private int confrontos;

    @Column(name = "winsUserA", nullable = false)
    private int vitoriasUsuarioA;

    @Column(name = "winsUserB", nullable = false)
    private int vitoriasUsuarioB;

    @Column(name = "draws", nullable = false)
    private int empates;

    @Column(name = "goalsUserA", nullable = false)
    private int golsUsuarioA;

    @Column(name = "goalsUserB", nullable = false)
    private int golsUsuarioB;

    @Column(name = "campeonatosDisputados", nullable = false)
    private int campeonatosDisputados;

    @Column(name = "titulosUsuarioA", nullable = false)
    private int titulosUsuarioA;

    @Column(name = "titulosUsuarioB", nullable = false)
    private int titulosUsuarioB;

    /** Maior goleada aplicada pelo usuário A (persistida incrementalmente). */
    @Column(name = "maior_goleada_a_gols_pro", nullable = false)
    private int maiorGoleadaAGolsPro;

    @Column(name = "maior_goleada_a_gols_contra", nullable = false)
    private int maiorGoleadaAGolsContra;

    @Column(name = "maior_goleada_a_margem", nullable = false)
    private int maiorGoleadaAMargem;

    @Column(name = "maior_goleada_a_clube_pro", length = 120)
    private String maiorGoleadaAClubePro;

    @Column(name = "maior_goleada_a_clube_contra", length = 120)
    private String maiorGoleadaAClubeContra;

    @Column(name = "maior_goleada_a_campeonato_nome", length = 120)
    private String maiorGoleadaACampeonatoNome;

    /** Maior goleada aplicada pelo usuário B (persistida incrementalmente). */
    @Column(name = "maior_goleada_b_gols_pro", nullable = false)
    private int maiorGoleadaBGolsPro;

    @Column(name = "maior_goleada_b_gols_contra", nullable = false)
    private int maiorGoleadaBGolsContra;

    @Column(name = "maior_goleada_b_margem", nullable = false)
    private int maiorGoleadaBMargem;

    @Column(name = "maior_goleada_b_clube_pro", length = 120)
    private String maiorGoleadaBClubePro;

    @Column(name = "maior_goleada_b_clube_contra", length = 120)
    private String maiorGoleadaBClubeContra;

    @Column(name = "maior_goleada_b_campeonato_nome", length = 120)
    private String maiorGoleadaBCampeonatoNome;

    @Column(name = "maiorSequenciaVitoriasA", nullable = false)
    private int maiorSequenciaVitoriasA;

    @Column(name = "maiorSequenciaVitoriasB", nullable = false)
    private int maiorSequenciaVitoriasB;

    @Column(name = "sequenciaAtual", nullable = false)
    private int sequenciaAtual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sequencia_atual_user_id")
    private User sequenciaAtualUsuario;

    @Column(name = "lastMatch")
    private LocalDateTime dataUltimoConfronto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_winner_user_id")
    private User ultimoVencedor;

    public RivalidadeUsuario() {
    }

    public Long getRivalidadeUsuarioId() {
        return rivalidadeUsuarioId;
    }

    public void setRivalidadeUsuarioId(Long rivalidadeUsuarioId) {
        this.rivalidadeUsuarioId = rivalidadeUsuarioId;
    }

    public User getUsuarioA() {
        return usuarioA;
    }

    public void setUsuarioA(User usuarioA) {
        this.usuarioA = usuarioA;
    }

    public User getUsuarioB() {
        return usuarioB;
    }

    public void setUsuarioB(User usuarioB) {
        this.usuarioB = usuarioB;
    }

    public int getConfrontos() {
        return confrontos;
    }

    public void setConfrontos(int confrontos) {
        this.confrontos = confrontos;
    }

    public int getVitoriasUsuarioA() {
        return vitoriasUsuarioA;
    }

    public void setVitoriasUsuarioA(int vitoriasUsuarioA) {
        this.vitoriasUsuarioA = vitoriasUsuarioA;
    }

    public int getVitoriasUsuarioB() {
        return vitoriasUsuarioB;
    }

    public void setVitoriasUsuarioB(int vitoriasUsuarioB) {
        this.vitoriasUsuarioB = vitoriasUsuarioB;
    }

    public int getEmpates() {
        return empates;
    }

    public void setEmpates(int empates) {
        this.empates = empates;
    }

    public int getGolsUsuarioA() {
        return golsUsuarioA;
    }

    public void setGolsUsuarioA(int golsUsuarioA) {
        this.golsUsuarioA = golsUsuarioA;
    }

    public int getGolsUsuarioB() {
        return golsUsuarioB;
    }

    public void setGolsUsuarioB(int golsUsuarioB) {
        this.golsUsuarioB = golsUsuarioB;
    }

    public int getCampeonatosDisputados() {
        return campeonatosDisputados;
    }

    public void setCampeonatosDisputados(int campeonatosDisputados) {
        this.campeonatosDisputados = campeonatosDisputados;
    }

    public int getTitulosUsuarioA() {
        return titulosUsuarioA;
    }

    public void setTitulosUsuarioA(int titulosUsuarioA) {
        this.titulosUsuarioA = titulosUsuarioA;
    }

    public int getTitulosUsuarioB() {
        return titulosUsuarioB;
    }

    public void setTitulosUsuarioB(int titulosUsuarioB) {
        this.titulosUsuarioB = titulosUsuarioB;
    }

    public int getMaiorGoleadaAGolsPro() {
        return maiorGoleadaAGolsPro;
    }

    public void setMaiorGoleadaAGolsPro(int maiorGoleadaAGolsPro) {
        this.maiorGoleadaAGolsPro = maiorGoleadaAGolsPro;
    }

    public int getMaiorGoleadaAGolsContra() {
        return maiorGoleadaAGolsContra;
    }

    public void setMaiorGoleadaAGolsContra(int maiorGoleadaAGolsContra) {
        this.maiorGoleadaAGolsContra = maiorGoleadaAGolsContra;
    }

    public int getMaiorGoleadaAMargem() {
        return maiorGoleadaAMargem;
    }

    public void setMaiorGoleadaAMargem(int maiorGoleadaAMargem) {
        this.maiorGoleadaAMargem = maiorGoleadaAMargem;
    }

    public String getMaiorGoleadaAClubePro() {
        return maiorGoleadaAClubePro;
    }

    public void setMaiorGoleadaAClubePro(String maiorGoleadaAClubePro) {
        this.maiorGoleadaAClubePro = maiorGoleadaAClubePro;
    }

    public String getMaiorGoleadaAClubeContra() {
        return maiorGoleadaAClubeContra;
    }

    public void setMaiorGoleadaAClubeContra(String maiorGoleadaAClubeContra) {
        this.maiorGoleadaAClubeContra = maiorGoleadaAClubeContra;
    }

    public String getMaiorGoleadaACampeonatoNome() {
        return maiorGoleadaACampeonatoNome;
    }

    public void setMaiorGoleadaACampeonatoNome(String maiorGoleadaACampeonatoNome) {
        this.maiorGoleadaACampeonatoNome = maiorGoleadaACampeonatoNome;
    }

    public int getMaiorGoleadaBGolsPro() {
        return maiorGoleadaBGolsPro;
    }

    public void setMaiorGoleadaBGolsPro(int maiorGoleadaBGolsPro) {
        this.maiorGoleadaBGolsPro = maiorGoleadaBGolsPro;
    }

    public int getMaiorGoleadaBGolsContra() {
        return maiorGoleadaBGolsContra;
    }

    public void setMaiorGoleadaBGolsContra(int maiorGoleadaBGolsContra) {
        this.maiorGoleadaBGolsContra = maiorGoleadaBGolsContra;
    }

    public int getMaiorGoleadaBMargem() {
        return maiorGoleadaBMargem;
    }

    public void setMaiorGoleadaBMargem(int maiorGoleadaBMargem) {
        this.maiorGoleadaBMargem = maiorGoleadaBMargem;
    }

    public String getMaiorGoleadaBClubePro() {
        return maiorGoleadaBClubePro;
    }

    public void setMaiorGoleadaBClubePro(String maiorGoleadaBClubePro) {
        this.maiorGoleadaBClubePro = maiorGoleadaBClubePro;
    }

    public String getMaiorGoleadaBClubeContra() {
        return maiorGoleadaBClubeContra;
    }

    public void setMaiorGoleadaBClubeContra(String maiorGoleadaBClubeContra) {
        this.maiorGoleadaBClubeContra = maiorGoleadaBClubeContra;
    }

    public String getMaiorGoleadaBCampeonatoNome() {
        return maiorGoleadaBCampeonatoNome;
    }

    public void setMaiorGoleadaBCampeonatoNome(String maiorGoleadaBCampeonatoNome) {
        this.maiorGoleadaBCampeonatoNome = maiorGoleadaBCampeonatoNome;
    }

    public int getMaiorSequenciaVitoriasA() {
        return maiorSequenciaVitoriasA;
    }

    public void setMaiorSequenciaVitoriasA(int maiorSequenciaVitoriasA) {
        this.maiorSequenciaVitoriasA = maiorSequenciaVitoriasA;
    }

    public int getMaiorSequenciaVitoriasB() {
        return maiorSequenciaVitoriasB;
    }

    public void setMaiorSequenciaVitoriasB(int maiorSequenciaVitoriasB) {
        this.maiorSequenciaVitoriasB = maiorSequenciaVitoriasB;
    }

    public int getSequenciaAtual() {
        return sequenciaAtual;
    }

    public void setSequenciaAtual(int sequenciaAtual) {
        this.sequenciaAtual = sequenciaAtual;
    }

    public User getSequenciaAtualUsuario() {
        return sequenciaAtualUsuario;
    }

    public void setSequenciaAtualUsuario(User sequenciaAtualUsuario) {
        this.sequenciaAtualUsuario = sequenciaAtualUsuario;
    }

    public LocalDateTime getDataUltimoConfronto() {
        return dataUltimoConfronto;
    }

    public void setDataUltimoConfronto(LocalDateTime dataUltimoConfronto) {
        this.dataUltimoConfronto = dataUltimoConfronto;
    }

    public User getUltimoVencedor() {
        return ultimoVencedor;
    }

    public void setUltimoVencedor(User ultimoVencedor) {
        this.ultimoVencedor = ultimoVencedor;
    }
}
