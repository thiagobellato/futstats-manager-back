package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoComposicaoResponseDTO {

    private Integer quantidadeParticipantes;
    private Boolean possuiCampeaoProtegido;
    private Integer vagasCampeaoProtegido;
    private Integer vagasParaSelecao;
    private Map<ClubRank, Long> quantidadePorRankParaSelecao;
    private Long totalClubesDisponiveisParaSelecao;

    public Integer getQuantidadeParticipantes() {
        return quantidadeParticipantes;
    }

    public void setQuantidadeParticipantes(Integer quantidadeParticipantes) {
        this.quantidadeParticipantes = quantidadeParticipantes;
    }

    public Boolean getPossuiCampeaoProtegido() {
        return possuiCampeaoProtegido;
    }

    public void setPossuiCampeaoProtegido(Boolean possuiCampeaoProtegido) {
        this.possuiCampeaoProtegido = possuiCampeaoProtegido;
    }

    public Integer getVagasCampeaoProtegido() {
        return vagasCampeaoProtegido;
    }

    public void setVagasCampeaoProtegido(Integer vagasCampeaoProtegido) {
        this.vagasCampeaoProtegido = vagasCampeaoProtegido;
    }

    public Integer getVagasParaSelecao() {
        return vagasParaSelecao;
    }

    public void setVagasParaSelecao(Integer vagasParaSelecao) {
        this.vagasParaSelecao = vagasParaSelecao;
    }

    public Map<ClubRank, Long> getQuantidadePorRankParaSelecao() {
        return quantidadePorRankParaSelecao;
    }

    public void setQuantidadePorRankParaSelecao(Map<ClubRank, Long> quantidadePorRankParaSelecao) {
        this.quantidadePorRankParaSelecao = quantidadePorRankParaSelecao;
    }

    public Long getTotalClubesDisponiveisParaSelecao() {
        return totalClubesDisponiveisParaSelecao;
    }

    public void setTotalClubesDisponiveisParaSelecao(Long totalClubesDisponiveisParaSelecao) {
        this.totalClubesDisponiveisParaSelecao = totalClubesDisponiveisParaSelecao;
    }
}
