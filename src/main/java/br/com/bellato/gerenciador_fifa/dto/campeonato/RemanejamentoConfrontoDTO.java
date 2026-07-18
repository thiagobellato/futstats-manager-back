package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.List;

public class RemanejamentoConfrontoDTO {

    private Long clubeCompetidorVantagemId;
    private Long clubeCompetidorMinoriaId;

    public Long getClubeCompetidorVantagemId() {
        return clubeCompetidorVantagemId;
    }

    public void setClubeCompetidorVantagemId(Long clubeCompetidorVantagemId) {
        this.clubeCompetidorVantagemId = clubeCompetidorVantagemId;
    }

    public Long getClubeCompetidorMinoriaId() {
        return clubeCompetidorMinoriaId;
    }

    public void setClubeCompetidorMinoriaId(Long clubeCompetidorMinoriaId) {
        this.clubeCompetidorMinoriaId = clubeCompetidorMinoriaId;
    }
}
