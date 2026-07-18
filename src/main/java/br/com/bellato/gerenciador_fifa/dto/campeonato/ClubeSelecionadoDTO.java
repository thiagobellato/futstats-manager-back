package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubeSelecionadoDTO {

    private Long clubeId;
    private ClubRank rank;

    public Long getClubeId() {
        return clubeId;
    }

    public void setClubeId(Long clubeId) {
        this.clubeId = clubeId;
    }

    public ClubRank getRank() {
        return rank;
    }

    public void setRank(ClubRank rank) {
        this.rank = rank;
    }
}
