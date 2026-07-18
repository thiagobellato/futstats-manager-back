package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubesPorRankResponseDTO {

    private Map<ClubRank, List<ClubeDisponivelDTO>> clubesPorRank;

    public Map<ClubRank, List<ClubeDisponivelDTO>> getClubesPorRank() {
        return clubesPorRank;
    }

    public void setClubesPorRank(Map<ClubRank, List<ClubeDisponivelDTO>> clubesPorRank) {
        this.clubesPorRank = clubesPorRank;
    }
}
