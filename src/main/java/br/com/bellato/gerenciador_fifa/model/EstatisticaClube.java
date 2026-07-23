package br.com.bellato.gerenciador_fifa.model;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.ClubRank.ClubRankConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Estatísticas globais / de carreira do clube (1:1 com {@link Clube}).
 * Espelha o papel de {@link EstatisticaAtleta} para o domínio de clubes.
 */
@Entity
@Table(name = "estatisticas_clube")
public class EstatisticaClube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clube_id", nullable = false, unique = true)
    private Clube clube;

    @Column(name = "clubeRank")
    @Convert(converter = ClubRankConverter.class)
    private ClubRank rank;

    @Column(name = "clubeGolsPro")
    private Integer golsPro = 0;

    @Column(name = "clubeGolsContra")
    private Integer golsContra = 0;

    @Column(name = "clubeVitorias")
    private Integer vitorias = 0;

    @Column(name = "clubeEmpates")
    private Integer empates = 0;

    @Column(name = "clubeDerrotas")
    private Integer derrotas = 0;

    @Column(name = "clubeTitulos")
    private Integer titulos = 0;

    public EstatisticaClube() {
    }

    public static EstatisticaClube zerada(Clube clube) {
        EstatisticaClube estatistica = new EstatisticaClube();
        estatistica.setClube(clube);
        estatistica.setGolsPro(0);
        estatistica.setGolsContra(0);
        estatistica.setVitorias(0);
        estatistica.setEmpates(0);
        estatistica.setDerrotas(0);
        estatistica.setTitulos(0);
        return estatistica;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Clube getClube() {
        return clube;
    }

    public void setClube(Clube clube) {
        this.clube = clube;
    }

    public ClubRank getRank() {
        return rank;
    }

    public void setRank(ClubRank rank) {
        this.rank = rank;
    }

    public Integer getGolsPro() {
        return golsPro;
    }

    public void setGolsPro(Integer golsPro) {
        this.golsPro = golsPro;
    }

    public Integer getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(Integer golsContra) {
        this.golsContra = golsContra;
    }

    public Integer getVitorias() {
        return vitorias;
    }

    public void setVitorias(Integer vitorias) {
        this.vitorias = vitorias;
    }

    public Integer getEmpates() {
        return empates;
    }

    public void setEmpates(Integer empates) {
        this.empates = empates;
    }

    public Integer getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(Integer derrotas) {
        this.derrotas = derrotas;
    }

    public Integer getTitulos() {
        return titulos;
    }

    public void setTitulos(Integer titulos) {
        this.titulos = titulos;
    }

    public int getSaldoGols() {
        int pro = golsPro == null ? 0 : golsPro;
        int contra = golsContra == null ? 0 : golsContra;
        return pro - contra;
    }

    @Override
    public String toString() {
        return "EstatisticaClube [id=" + id + ", rank=" + rank + ", golsPro=" + golsPro
                + ", golsContra=" + golsContra + ", vitorias=" + vitorias + ", empates=" + empates
                + ", derrotas=" + derrotas + ", titulos=" + titulos + "]";
    }
}
