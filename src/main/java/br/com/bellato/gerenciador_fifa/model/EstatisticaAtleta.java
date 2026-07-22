package br.com.bellato.gerenciador_fifa.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "estatisticas_atleta")
public class EstatisticaAtleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Atleta atleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clube_id", nullable = false)
    private Clube clube;

    @Column(name = "atletaGols")
    private Integer gols;

    @Column(name = "atletaCartaoAmarelo")
    private Integer cartaoAmarelo;

    @Column(name = "atletaCartaoVermelho")
    private Integer cartaoVermelho;

    @Column(name = "atletaAssistencias")
    private Integer assistencias;

    @Column(name = "atletaGolsContra")
    private Integer golsContra;

    @Column(name = "atletaDataInicio")
    private LocalDate dataInicio;

    @Column(name = "atletaDataFim")
    private LocalDate dataFim;

    public EstatisticaAtleta() {
    }

    public EstatisticaAtleta(Long id, Atleta atleta, Clube clube, Integer gols, Integer cartaoAmarelo,
            Integer cartaoVermelho, Integer assistencias, LocalDate dataInicio, LocalDate dataFim) {
        this.id = id;
        this.atleta = atleta;
        this.clube = clube;
        this.gols = gols;
        this.cartaoAmarelo = cartaoAmarelo;
        this.cartaoVermelho = cartaoVermelho;
        this.assistencias = assistencias;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public EstatisticaAtleta(Long id, Atleta atleta, Clube clube, Integer gols, Integer cartaoAmarelo,
            Integer cartaoVermelho, Integer assistencias, Integer golsContra, LocalDate dataInicio, LocalDate dataFim) {
        this(id, atleta, clube, gols, cartaoAmarelo, cartaoVermelho, assistencias, dataInicio, dataFim);
        this.golsContra = golsContra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Atleta getAtleta() {
        return atleta;
    }

    public void setAtleta(Atleta atleta) {
        this.atleta = atleta;
    }

    public Clube getClube() {
        return clube;
    }

    public void setClube(Clube clube) {
        this.clube = clube;
    }

    public Integer getGols() {
        return gols;
    }

    public void setGols(Integer gols) {
        this.gols = gols;
    }

    public Integer getCartaoAmarelo() {
        return cartaoAmarelo;
    }

    public void setCartaoAmarelo(Integer cartaoAmarelo) {
        this.cartaoAmarelo = cartaoAmarelo;
    }

    public Integer getCartaoVermelho() {
        return cartaoVermelho;
    }

    public void setCartaoVermelho(Integer cartaoVermelho) {
        this.cartaoVermelho = cartaoVermelho;
    }

    public Integer getAssistencias() {
        return assistencias;
    }

    public void setAssistencias(Integer assistencias) {
        this.assistencias = assistencias;
    }

    public Integer getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(Integer golsContra) {
        this.golsContra = golsContra;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    @Override
    public String toString() {
        return "EstatisticaAtleta [id=" + id + ", atleta=" + atleta + ", clube=" + clube + ", gols=" + gols
                + ", cartaoAmarelo=" + cartaoAmarelo + ", cartaoVermelho=" + cartaoVermelho + ", assistencias="
                + assistencias + ", dataInicio=" + dataInicio + ", dataFim=" + dataFim + "]";
    }

}
