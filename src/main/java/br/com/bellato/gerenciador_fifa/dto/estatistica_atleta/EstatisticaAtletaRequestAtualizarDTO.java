package br.com.bellato.gerenciador_fifa.dto.estatistica_atleta;

import com.fasterxml.jackson.annotation.JsonInclude;

public class EstatisticaAtletaRequestAtualizarDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long atletaId;
    private Long clubeId;
    private Integer gols;
    private Integer assistencias;

    public Long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
    }

    public Long getClubeId() {
        return clubeId;
    }

    public void setClubeId(Long clubeId) {
        this.clubeId = clubeId;
    }

    public Integer getGols() {
        return gols;
    }

    public void setGols(Integer gols) {
        this.gols = gols;
    }

    public Integer getAssistencias() {
        return assistencias;
    }

    public void setAssistencias(Integer assistencias) {
        this.assistencias = assistencias;
    }

}
