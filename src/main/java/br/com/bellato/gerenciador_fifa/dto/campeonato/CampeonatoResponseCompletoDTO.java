package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoResponseCompletoDTO {

    private Long campeonatoId;
    private String nome;
    private Integer quantidadeClubes;
    private StatusCampeonato status;
    private String competidor1Nome;
    private String competidor2Nome;
    private Boolean possuiCampeaoAnterior;
    private Integer campeaoAnteriorCompetidor;
    private Long campeaoAnteriorClubeOrigemId;
    private String campeaoAnteriorClubeNome;
    private LocalDateTime dataCriacao;
    private Map<ClubRank, Integer> distribuicaoRanks;
    private List<CampeonatoClubeResponseDTO> clubes;
    private Long quantidadeAtletas;

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

    public String getCampeaoAnteriorClubeNome() {
        return campeaoAnteriorClubeNome;
    }

    public void setCampeaoAnteriorClubeNome(String campeaoAnteriorClubeNome) {
        this.campeaoAnteriorClubeNome = campeaoAnteriorClubeNome;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Map<ClubRank, Integer> getDistribuicaoRanks() {
        return distribuicaoRanks;
    }

    public void setDistribuicaoRanks(Map<ClubRank, Integer> distribuicaoRanks) {
        this.distribuicaoRanks = distribuicaoRanks;
    }

    public List<CampeonatoClubeResponseDTO> getClubes() {
        return clubes;
    }

    public void setClubes(List<CampeonatoClubeResponseDTO> clubes) {
        this.clubes = clubes;
    }

    public Long getQuantidadeAtletas() {
        return quantidadeAtletas;
    }

    public void setQuantidadeAtletas(Long quantidadeAtletas) {
        this.quantidadeAtletas = quantidadeAtletas;
    }
}
