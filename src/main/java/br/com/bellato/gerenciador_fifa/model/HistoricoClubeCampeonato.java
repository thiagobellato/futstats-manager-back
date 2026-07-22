package br.com.bellato.gerenciador_fifa.model;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "historico_clube_campeonato", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "clube_id", "campeonato_id" })
})
public class HistoricoClubeCampeonato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historicoClubeCampeonatoId")
    private Long historicoClubeCampeonatoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "clube_id", nullable = false)
    private Clube clube;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @Column(name = "historicoClubePosicaoFinal")
    private Integer posicaoFinal;

    @Column(name = "historicoClubeRankAnterior", nullable = false)
    @Convert(converter = ClubRankConverter.class)
    private ClubRank rankAnterior;

    @Column(name = "historicoClubeRankNovo", nullable = false)
    @Convert(converter = ClubRankConverter.class)
    private ClubRank rankNovo;

    @Column(name = "historicoClubeTituloConquistado", nullable = false)
    private Boolean tituloConquistado = Boolean.FALSE;

    @Column(name = "historicoClubeEliminado", nullable = false)
    private Boolean eliminado = Boolean.FALSE;

    @Column(name = "historicoClubeFaseAlcancada")
    private Integer faseAlcancada;

    @Column(name = "historicoClubeJogos")
    private Integer jogos = 0;

    @Column(name = "historicoClubeVitorias")
    private Integer vitorias = 0;

    @Column(name = "historicoClubeEmpates")
    private Integer empates = 0;

    @Column(name = "historicoClubeDerrotas")
    private Integer derrotas = 0;

    @Column(name = "historicoClubeGolsPro")
    private Integer golsPro = 0;

    @Column(name = "historicoClubeGolsContra")
    private Integer golsContra = 0;

    public Long getHistoricoClubeCampeonatoId() {
        return historicoClubeCampeonatoId;
    }

    public void setHistoricoClubeCampeonatoId(Long historicoClubeCampeonatoId) {
        this.historicoClubeCampeonatoId = historicoClubeCampeonatoId;
    }

    public Clube getClube() {
        return clube;
    }

    public void setClube(Clube clube) {
        this.clube = clube;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public Integer getPosicaoFinal() {
        return posicaoFinal;
    }

    public void setPosicaoFinal(Integer posicaoFinal) {
        this.posicaoFinal = posicaoFinal;
    }

    public ClubRank getRankAnterior() {
        return rankAnterior;
    }

    public void setRankAnterior(ClubRank rankAnterior) {
        this.rankAnterior = rankAnterior;
    }

    public ClubRank getRankNovo() {
        return rankNovo;
    }

    public void setRankNovo(ClubRank rankNovo) {
        this.rankNovo = rankNovo;
    }

    public Boolean getTituloConquistado() {
        return tituloConquistado;
    }

    public void setTituloConquistado(Boolean tituloConquistado) {
        this.tituloConquistado = tituloConquistado;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Integer getFaseAlcancada() {
        return faseAlcancada;
    }

    public void setFaseAlcancada(Integer faseAlcancada) {
        this.faseAlcancada = faseAlcancada;
    }

    public Integer getJogos() {
        return jogos;
    }

    public void setJogos(Integer jogos) {
        this.jogos = jogos;
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
}
