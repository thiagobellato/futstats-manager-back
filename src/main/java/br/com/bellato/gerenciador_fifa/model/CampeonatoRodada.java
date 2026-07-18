package br.com.bellato.gerenciador_fifa.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    public List<CampeonatoPartida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<CampeonatoPartida> partidas) {
        this.partidas = partidas;
    }
}
