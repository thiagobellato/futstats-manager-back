package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.ArrayList;
import java.util.List;

public class RegistrarPartidaRequestDTO {

    private Integer golsMandante;
    private Integer golsVisitante;
    private Boolean disputouPenaltis;
    private Integer penaltisMandante;
    private Integer penaltisVisitante;
    private List<PartidaEventoRequestDTO> eventos = new ArrayList<>();

    public Integer getGolsMandante() {
        return golsMandante;
    }

    public void setGolsMandante(Integer golsMandante) {
        this.golsMandante = golsMandante;
    }

    public Integer getGolsVisitante() {
        return golsVisitante;
    }

    public void setGolsVisitante(Integer golsVisitante) {
        this.golsVisitante = golsVisitante;
    }

    public Boolean getDisputouPenaltis() {
        return disputouPenaltis;
    }

    public void setDisputouPenaltis(Boolean disputouPenaltis) {
        this.disputouPenaltis = disputouPenaltis;
    }

    public Integer getPenaltisMandante() {
        return penaltisMandante;
    }

    public void setPenaltisMandante(Integer penaltisMandante) {
        this.penaltisMandante = penaltisMandante;
    }

    public Integer getPenaltisVisitante() {
        return penaltisVisitante;
    }

    public void setPenaltisVisitante(Integer penaltisVisitante) {
        this.penaltisVisitante = penaltisVisitante;
    }

    public List<PartidaEventoRequestDTO> getEventos() {
        return eventos;
    }

    public void setEventos(List<PartidaEventoRequestDTO> eventos) {
        this.eventos = eventos;
    }
}
