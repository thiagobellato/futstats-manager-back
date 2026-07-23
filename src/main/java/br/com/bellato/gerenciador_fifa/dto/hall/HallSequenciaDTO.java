package br.com.bellato.gerenciador_fifa.dto.hall;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallSequenciaDTO {

    private Long clubeOrigemId;
    private String clubeNome;
    private Integer sequencia;
    private String tipo;

    public HallSequenciaDTO() {
    }

    public HallSequenciaDTO(Long clubeOrigemId, String clubeNome, Integer sequencia, String tipo) {
        this.clubeOrigemId = clubeOrigemId;
        this.clubeNome = clubeNome;
        this.sequencia = sequencia;
        this.tipo = tipo;
    }

    public Long getClubeOrigemId() {
        return clubeOrigemId;
    }

    public void setClubeOrigemId(Long clubeOrigemId) {
        this.clubeOrigemId = clubeOrigemId;
    }

    public String getClubeNome() {
        return clubeNome;
    }

    public void setClubeNome(String clubeNome) {
        this.clubeNome = clubeNome;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
