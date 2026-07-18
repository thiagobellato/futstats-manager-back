package br.com.bellato.gerenciador_fifa.model;

import java.util.ArrayList;
import java.util.List;

import br.com.bellato.gerenciador_fifa.enums.StatusRodada;
import br.com.bellato.gerenciador_fifa.enums.StatusRodada.StatusRodadaConverter;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "campeonato_rodada")
public class CampeonatoRodada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoRodadaId")
    private Long campeonatoRodadaId;

    @ManyToOne
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @Column(name = "campeonatoRodadaNumero")
    private Integer numeroRodada;

    @Column(name = "campeonatoRodadaNome")
    private String nome;

    @Column(name = "campeonatoRodadaStatus")
    @Convert(converter = StatusRodadaConverter.class)
    private StatusRodada status;

    @Column(name = "campeonatoRodadaFaseAssincrona")
    private Boolean faseAssincrona = Boolean.FALSE;

    @Column(name = "campeonatoRodadaCompetidorRemanejamento")
    private Integer competidorRemanejamento;

    @OneToMany(mappedBy = "campeonatoRodada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampeonatoPartida> partidas = new ArrayList<>();

    public CampeonatoRodada() {
    }

    public Long getCampeonatoRodadaId() {
        return campeonatoRodadaId;
    }

    public void setCampeonatoRodadaId(Long campeonatoRodadaId) {
        this.campeonatoRodadaId = campeonatoRodadaId;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
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

    public List<CampeonatoPartida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<CampeonatoPartida> partidas) {
        this.partidas = partidas;
    }
}
