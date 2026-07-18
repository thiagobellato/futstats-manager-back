package br.com.bellato.gerenciador_fifa.dto.clube;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubeResumoRankResponseDTO {

    private Map<ClubRank, Long> quantidadePorRank;
    private Long totalClubes;

    public Map<ClubRank, Long> getQuantidadePorRank() {
        return quantidadePorRank;
    }

    public void setQuantidadePorRank(Map<ClubRank, Long> quantidadePorRank) {
        this.quantidadePorRank = quantidadePorRank;
    }

    public Long getTotalClubes() {
        return totalClubes;
    }

    public void setTotalClubes(Long totalClubes) {
        this.totalClubes = totalClubes;
    }
}
