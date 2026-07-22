package br.com.bellato.gerenciador_fifa.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato.StatusCampeonatoConverter;
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
@Table(name = "campeonato")
public class Campeonato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoId")
    private Long campeonatoId;

    @Column(name = "campeonatoNome", nullable = false)
    private String nome;

    @Column(name = "campeonatoQuantidadeClubes", nullable = false)
    private Integer quantidadeClubes;

    @Column(name = "campeonatoStatus", nullable = false)
    @Convert(converter = StatusCampeonatoConverter.class)
    private StatusCampeonato status;

    @Column(name = "campeonatoCompetidor1Nome")
    private String competidor1Nome;

    @Column(name = "campeonatoCompetidor2Nome")
    private String competidor2Nome;

    @Column(name = "campeonatoPossuiCampeaoAnterior")
    private Boolean possuiCampeaoAnterior;

    @Column(name = "campeonatoCampeaoAnteriorCompetidor")
    private Integer campeaoAnteriorCompetidor;

    @Column(name = "campeonatoCampeaoAnteriorClubeOrigemId")
    private Long campeaoAnteriorClubeOrigemId;

    @Column(name = "campeonatoDataCriacao")
    private LocalDateTime dataCriacao;

    @Column(name = "campeonatoDataFinalizacao")
    private LocalDateTime dataFinalizacao;

    @Column(name = "campeonatoRodadaAtual")
    private Integer rodadaAtual;

    @Column(name = "campeonatoCampeaoCompetidor")
    private Integer campeaoCompetidor;

    @ManyToOne
    @JoinColumn(name = "campeonato_campeao_clube_id")
    private CampeonatoClube campeaoClube;

    @OneToMany(mappedBy = "campeonato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampeonatoDistribuicaoRank> distribuicaoRanks = new ArrayList<>();

    @OneToMany(mappedBy = "campeonato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampeonatoClube> clubes = new ArrayList<>();

    @OneToMany(mappedBy = "campeonato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampeonatoAtleta> atletas = new ArrayList<>();

    @OneToMany(mappedBy = "campeonato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampeonatoRodada> rodadas = new ArrayList<>();

    public Campeonato() {
    }

    public Long getCampeonatoId() {
        return campeonatoId;
    }

    public void setCampeonatoId(Long campeonatoId) {
        this.campeonatoId = campeonatoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidadeClubes() {
        return quantidadeClubes;
    }

    public void setQuantidadeClubes(Integer quantidadeClubes) {
        this.quantidadeClubes = quantidadeClubes;
    }

    public StatusCampeonato getStatus() {
        return status;
    }

    public void setStatus(StatusCampeonato status) {
        this.status = status;
    }

    public String getCompetidor1Nome() {
        return competidor1Nome;
    }

    public void setCompetidor1Nome(String competidor1Nome) {
        this.competidor1Nome = competidor1Nome;
    }

    public String getCompetidor2Nome() {
        return competidor2Nome;
    }

    public void setCompetidor2Nome(String competidor2Nome) {
        this.competidor2Nome = competidor2Nome;
    }

    public Boolean getPossuiCampeaoAnterior() {
        return possuiCampeaoAnterior;
    }

    public void setPossuiCampeaoAnterior(Boolean possuiCampeaoAnterior) {
        this.possuiCampeaoAnterior = possuiCampeaoAnterior;
    }

    public Integer getCampeaoAnteriorCompetidor() {
        return campeaoAnteriorCompetidor;
    }

    public void setCampeaoAnteriorCompetidor(Integer campeaoAnteriorCompetidor) {
        this.campeaoAnteriorCompetidor = campeaoAnteriorCompetidor;
    }

    public Long getCampeaoAnteriorClubeOrigemId() {
        return campeaoAnteriorClubeOrigemId;
    }

    public void setCampeaoAnteriorClubeOrigemId(Long campeaoAnteriorClubeOrigemId) {
        this.campeaoAnteriorClubeOrigemId = campeaoAnteriorClubeOrigemId;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public Integer getRodadaAtual() {
        return rodadaAtual;
    }

    public void setRodadaAtual(Integer rodadaAtual) {
        this.rodadaAtual = rodadaAtual;
    }

    public Integer getCampeaoCompetidor() {
        return campeaoCompetidor;
    }

    public void setCampeaoCompetidor(Integer campeaoCompetidor) {
        this.campeaoCompetidor = campeaoCompetidor;
    }

    public CampeonatoClube getCampeaoClube() {
        return campeaoClube;
    }

    public void setCampeaoClube(CampeonatoClube campeaoClube) {
        this.campeaoClube = campeaoClube;
    }

    public List<CampeonatoDistribuicaoRank> getDistribuicaoRanks() {
        return distribuicaoRanks;
    }

    public void setDistribuicaoRanks(List<CampeonatoDistribuicaoRank> distribuicaoRanks) {
        this.distribuicaoRanks = distribuicaoRanks;
    }

    public List<CampeonatoClube> getClubes() {
        return clubes;
    }

    public void setClubes(List<CampeonatoClube> clubes) {
        this.clubes = clubes;
    }

    public List<CampeonatoAtleta> getAtletas() {
        return atletas;
    }

    public void setAtletas(List<CampeonatoAtleta> atletas) {
        this.atletas = atletas;
    }

    public List<CampeonatoRodada> getRodadas() {
        return rodadas;
    }

    public void setRodadas(List<CampeonatoRodada> rodadas) {
        this.rodadas = rodadas;
    }
}
