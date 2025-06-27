package br.com.bellato.gerenciador_fifa.dto.estatistica_atleta;

import java.time.LocalDate;

public class EstatisticaAtletaRequestDTO {

    private Long clube_id;
    private Long atleta_id;
    private Integer gols;
    private Integer assistencias;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public Long getClube_id() {
        return clube_id;
    }

    public void setClube_id(Long clube_id) {
        this.clube_id = clube_id;
    }

    public Long getAtleta_id() {
        return atleta_id;
    }

    public void setAtleta_id(Long atleta_id) {
        this.atleta_id = atleta_id;
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

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

}
