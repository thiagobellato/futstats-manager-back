package br.com.bellato.gerenciador_fifa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "campeonato_partida")
public class CampeonatoPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoPartidaId")
    private Long campeonatoPartidaId;

    @ManyToOne
    @JoinColumn(name = "campeonato_rodada_id", nullable = false)
    private CampeonatoRodada campeonatoRodada;

    @ManyToOne
    @JoinColumn(name = "campeonato_clube_mandante_id")
    private CampeonatoClube clubeMandante;

    @ManyToOne
    @JoinColumn(name = "campeonato_clube_visitante_id")
    private CampeonatoClube clubeVisitante;

    public CampeonatoPartida() {
    }

    public Long getCampeonatoPartidaId() {
        return campeonatoPartidaId;
    }

    public void setCampeonatoPartidaId(Long campeonatoPartidaId) {
        this.campeonatoPartidaId = campeonatoPartidaId;
    }

    public CampeonatoRodada getCampeonatoRodada() {
        return campeonatoRodada;
    }

    public void setCampeonatoRodada(CampeonatoRodada campeonatoRodada) {
        this.campeonatoRodada = campeonatoRodada;
    }

    public CampeonatoClube getClubeMandante() {
        return clubeMandante;
    }

    public void setClubeMandante(CampeonatoClube clubeMandante) {
        this.clubeMandante = clubeMandante;
    }

    public CampeonatoClube getClubeVisitante() {
        return clubeVisitante;
    }

    public void setClubeVisitante(CampeonatoClube clubeVisitante) {
        this.clubeVisitante = clubeVisitante;
    }
}
