package br.com.bellato.gerenciador_fifa.model;

import java.util.ArrayList;
import java.util.List;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.ClubRank.ClubRankConverter;
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
@Table(name = "campeonato_clube")
public class CampeonatoClube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoClubeId")
    private Long campeonatoClubeId;

    @ManyToOne
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @Column(name = "campeonatoClubeOrigemId", nullable = false)
    private Long clubeOrigemId;

    @Column(name = "campeonatoClubeNome", nullable = false)
    private String nome;

    @Column(name = "campeonatoClubeSigla")
    private String sigla;

    @Column(name = "campeonatoClubePais")
    private String pais;

    @Column(name = "campeonatoClubeRank", nullable = false)
    @Convert(converter = ClubRankConverter.class)
    private ClubRank rank;

    @Column(name = "campeonatoClubeCompetidorNumero")
    private Integer competidorNumero;

    @Column(name = "campeonatoClubeCampeaoAnterior")
    private Boolean campeaoAnterior;

    @Column(name = "campeonatoClubeExcluidoSorteio")
    private Boolean excluidoSorteio;

    @Column(name = "campeonatoClubeEliminado")
    private Boolean eliminado = Boolean.FALSE;

    @Column(name = "campeonatoClubeJogos")
    private Integer jogos = 0;

    @Column(name = "campeonatoClubeGolsPro")
    private Integer golsPro = 0;

    @Column(name = "campeonatoClubeGolsContra")
    private Integer golsContra = 0;

    @OneToMany(mappedBy = "campeonatoClube")
    private List<CampeonatoAtleta> atletas = new ArrayList<>();

    public CampeonatoClube() {
    }

    public Long getCampeonatoClubeId() {
        return campeonatoClubeId;
    }

    public void setCampeonatoClubeId(Long campeonatoClubeId) {
        this.campeonatoClubeId = campeonatoClubeId;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public Long getClubeOrigemId() {
        return clubeOrigemId;
    }

    public void setClubeOrigemId(Long clubeOrigemId) {
        this.clubeOrigemId = clubeOrigemId;
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

    public Integer getCompetidorNumero() {
        return competidorNumero;
    }

    public void setCompetidorNumero(Integer competidorNumero) {
        this.competidorNumero = competidorNumero;
    }

    public Boolean getCampeaoAnterior() {
        return campeaoAnterior;
    }

    public void setCampeaoAnterior(Boolean campeaoAnterior) {
        this.campeaoAnterior = campeaoAnterior;
    }

    public Boolean getExcluidoSorteio() {
        return excluidoSorteio;
    }

    public void setExcluidoSorteio(Boolean excluidoSorteio) {
        this.excluidoSorteio = excluidoSorteio;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Integer getJogos() {
        return jogos;
    }

    public void setJogos(Integer jogos) {
        this.jogos = jogos;
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

    public int getSaldoGols() {
        int pro = golsPro == null ? 0 : golsPro;
        int contra = golsContra == null ? 0 : golsContra;
        return pro - contra;
    }

    public boolean isClassificado() {
        return !Boolean.TRUE.equals(eliminado);
    }

    public List<CampeonatoAtleta> getAtletas() {
        return atletas;
    }

    public void setAtletas(List<CampeonatoAtleta> atletas) {
        this.atletas = atletas;
    }
}
