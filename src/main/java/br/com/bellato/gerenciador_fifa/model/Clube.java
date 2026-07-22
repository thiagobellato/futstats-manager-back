package br.com.bellato.gerenciador_fifa.model;

import java.util.List;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.ClubRank.ClubRankConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "clube")
public class Clube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clubeId")
    private long clubeId;

    @Column(name = "clubeNome")
    private String nome;

    @Column(name = "clubeSigla")
    private String sigla;

    @Column(name = "clubePais")
    private String pais;

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

    @OneToMany(mappedBy = "clube", cascade = CascadeType.ALL)
    private List<Atleta> atletas;

    public Clube() {
    }

    public Clube(long clubeId, String nome, String sigla, String pais) {
        this.clubeId = clubeId;
        this.nome = nome;
        this.sigla = sigla;
        this.pais = pais;
    }

    public long getClubeId() {
        return clubeId;
    }

    public void setClubeId(long clubeId) {
        this.clubeId = clubeId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
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
        return "Clube [clubeId=" + clubeId + ", nome=" + nome + ", sigla=" + sigla + ", pais=" + pais + "]";
    }

}
