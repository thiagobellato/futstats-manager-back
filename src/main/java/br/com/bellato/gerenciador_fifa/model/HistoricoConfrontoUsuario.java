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
 * Confronto consolidado entre dois participantes autenticados ao finalizar um campeonato.
 * Persistido uma vez — nunca recalculado. Alimenta a linha do tempo da rivalidade.
 */
@Entity
@Table(name = "historico_confronto_usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_historico_confronto_campeonato", columnNames = { "campeonato_id" })
})
public class HistoricoConfrontoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "headToHeadMatchId")
    private Long historicoConfrontoUsuarioId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_a_id", nullable = false)
    private CampeonatoParticipante participanteA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_b_id", nullable = false)
    private CampeonatoParticipante participanteB;

    /** Participante vencedor do campeonato (lado campeão). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_participant_id")
    private CampeonatoParticipante vencedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "champion_clube_id")
    private Clube clubeCampeao;

    @Column(name = "championshipName", nullable = false, length = 120)
    private String nomeCampeonato;

    @Column(name = "finishedAt", nullable = false)
    private LocalDateTime dataFinalizacao;

    @Column(name = "matchesPlayed", nullable = false)
    private int partidasDisputadas;

    @Column(name = "goalsParticipantA", nullable = false)
    private int golsParticipanteA;

    @Column(name = "goalsParticipantB", nullable = false)
    private int golsParticipanteB;

    @Column(name = "maiorGoleadaGolsA", nullable = false)
    private int maiorGoleadaGolsA;

    @Column(name = "maiorGoleadaGolsB", nullable = false)
    private int maiorGoleadaGolsB;

    public HistoricoConfrontoUsuario() {
    }

    public Long getHistoricoConfrontoUsuarioId() {
        return historicoConfrontoUsuarioId;
    }

    public void setHistoricoConfrontoUsuarioId(Long historicoConfrontoUsuarioId) {
        this.historicoConfrontoUsuarioId = historicoConfrontoUsuarioId;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public CampeonatoParticipante getParticipanteA() {
        return participanteA;
    }

    public void setParticipanteA(CampeonatoParticipante participanteA) {
        this.participanteA = participanteA;
    }

    public CampeonatoParticipante getParticipanteB() {
        return participanteB;
    }

    public void setParticipanteB(CampeonatoParticipante participanteB) {
        this.participanteB = participanteB;
    }

    public CampeonatoParticipante getVencedor() {
        return vencedor;
    }

    public void setVencedor(CampeonatoParticipante vencedor) {
        this.vencedor = vencedor;
    }

    public Clube getClubeCampeao() {
        return clubeCampeao;
    }

    public void setClubeCampeao(Clube clubeCampeao) {
        this.clubeCampeao = clubeCampeao;
    }

    public String getNomeCampeonato() {
        return nomeCampeonato;
    }

    public void setNomeCampeonato(String nomeCampeonato) {
        this.nomeCampeonato = nomeCampeonato;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public int getPartidasDisputadas() {
        return partidasDisputadas;
    }

    public void setPartidasDisputadas(int partidasDisputadas) {
        this.partidasDisputadas = partidasDisputadas;
    }

    public int getGolsParticipanteA() {
        return golsParticipanteA;
    }

    public void setGolsParticipanteA(int golsParticipanteA) {
        this.golsParticipanteA = golsParticipanteA;
    }

    public int getGolsParticipanteB() {
        return golsParticipanteB;
    }

    public void setGolsParticipanteB(int golsParticipanteB) {
        this.golsParticipanteB = golsParticipanteB;
    }

    public int getMaiorGoleadaGolsA() {
        return maiorGoleadaGolsA;
    }

    public void setMaiorGoleadaGolsA(int maiorGoleadaGolsA) {
        this.maiorGoleadaGolsA = maiorGoleadaGolsA;
    }

    public int getMaiorGoleadaGolsB() {
        return maiorGoleadaGolsB;
    }

    public void setMaiorGoleadaGolsB(int maiorGoleadaGolsB) {
        this.maiorGoleadaGolsB = maiorGoleadaGolsB;
    }
}
