package br.com.bellato.gerenciador_fifa.model;

import java.util.ArrayList;
import java.util.List;

import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida.StatusPartidaConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "campeonato_partida", indexes = {
        @Index(name = "idx_camp_partida_rodada", columnList = "campeonato_rodada_id")
})
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

    @ManyToOne
    @JoinColumn(name = "campeonato_clube_vencedor_id")
    private CampeonatoClube clubeVencedor;

    @Column(name = "campeonatoPartidaStatus")
    @Convert(converter = StatusPartidaConverter.class)
    private StatusPartida status;

    @Column(name = "campeonatoPartidaOrdem")
    private Integer ordem;

    @Column(name = "campeonatoPartidaGolsMandante")
    private Integer golsMandante;

    @Column(name = "campeonatoPartidaGolsVisitante")
    private Integer golsVisitante;

    @Column(name = "campeonatoPartidaDisputouPenaltis")
    private Boolean disputouPenaltis = Boolean.FALSE;

    @Column(name = "campeonatoPartidaPenaltisMandante")
    private Integer penaltisMandante;

    @Column(name = "campeonatoPartidaPenaltisVisitante")
    private Integer penaltisVisitante;

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    @BatchSize(size = 32)
    private List<CampeonatoPartidaEvento> eventos = new ArrayList<>();

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

    public CampeonatoClube getClubeVencedor() {
        return clubeVencedor;
    }

    public void setClubeVencedor(CampeonatoClube clubeVencedor) {
        this.clubeVencedor = clubeVencedor;
    }

    public StatusPartida getStatus() {
        return status;
    }

    public void setStatus(StatusPartida status) {
        this.status = status;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getGolsMandante() {
        return golsMandante;
    }

    public void setGolsMandante(Integer golsMandante) {
        this.golsMandante = golsMandante;
    }

    public Integer getGolsVisitante() {
        return golsVisitante;
    }

    public void setGolsVisitante(Integer golsVisitante) {
        this.golsVisitante = golsVisitante;
    }

    public Boolean getDisputouPenaltis() {
        return disputouPenaltis;
    }

    public void setDisputouPenaltis(Boolean disputouPenaltis) {
        this.disputouPenaltis = disputouPenaltis;
    }

    public Integer getPenaltisMandante() {
        return penaltisMandante;
    }

    public void setPenaltisMandante(Integer penaltisMandante) {
        this.penaltisMandante = penaltisMandante;
    }

    public Integer getPenaltisVisitante() {
        return penaltisVisitante;
    }

    public void setPenaltisVisitante(Integer penaltisVisitante) {
        this.penaltisVisitante = penaltisVisitante;
    }

    public List<CampeonatoPartidaEvento> getEventos() {
        return eventos;
    }

    public void setEventos(List<CampeonatoPartidaEvento> eventos) {
        this.eventos = eventos;
    }
}
