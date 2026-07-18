package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.List;

public class RemanejamentoRequestDTO {

    private List<RemanejamentoConfrontoDTO> confrontos;

    public List<RemanejamentoConfrontoDTO> getConfrontos() {
        return confrontos;
    }

    public void setConfrontos(List<RemanejamentoConfrontoDTO> confrontos) {
        this.confrontos = confrontos;
    }
}
