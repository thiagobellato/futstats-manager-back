package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoCriarRequestDTO {

    private String nome;
    private Integer quantidadeClubes;
    private String competidor1Nome;
    private String competidor2Nome;
    /** false/null = campeonato livre; true = participantes vinculados a usuários. */
    private Boolean autenticado;
    private Long competidor1UserId;
    private Long competidor2UserId;
    private Boolean possuiCampeaoAnterior;
    private Integer campeaoAnteriorCompetidor;
    private Long campeaoAnteriorClubeId;
    private Map<ClubRank, Integer> distribuicaoRanks;
    private List<ClubeSelecionadoDTO> clubesSelecionados;

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

    public Boolean getAutenticado() {
        return autenticado;
    }

    public void setAutenticado(Boolean autenticado) {
        this.autenticado = autenticado;
    }

    public Long getCompetidor1UserId() {
        return competidor1UserId;
    }

    public void setCompetidor1UserId(Long competidor1UserId) {
        this.competidor1UserId = competidor1UserId;
    }

    public Long getCompetidor2UserId() {
        return competidor2UserId;
    }

    public void setCompetidor2UserId(Long competidor2UserId) {
        this.competidor2UserId = competidor2UserId;
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

    public Long getCampeaoAnteriorClubeId() {
        return campeaoAnteriorClubeId;
    }

    public void setCampeaoAnteriorClubeId(Long campeaoAnteriorClubeId) {
        this.campeaoAnteriorClubeId = campeaoAnteriorClubeId;
    }

    public Map<ClubRank, Integer> getDistribuicaoRanks() {
        return distribuicaoRanks;
    }

    public void setDistribuicaoRanks(Map<ClubRank, Integer> distribuicaoRanks) {
        this.distribuicaoRanks = distribuicaoRanks;
    }

    public List<ClubeSelecionadoDTO> getClubesSelecionados() {
        return clubesSelecionados;
    }

    public void setClubesSelecionados(List<ClubeSelecionadoDTO> clubesSelecionados) {
        this.clubesSelecionados = clubesSelecionados;
    }
}
