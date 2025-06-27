package br.com.bellato.gerenciador_fifa.dto.atleta;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AtletaRequestDTO {

    private Long clubeId;
    private String nome;

    public Long getClubeId() {
        return clubeId;
    }

    public void setClubeId(Long clubeId) {
        this.clubeId = clubeId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}