package br.com.bellato.gerenciador_fifa.dto.atleta;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AtletaResponseDTO {

    private long atletaId;
    private String clubeNome;
    private String nome;

    public long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(long atletaId) {
        this.atletaId = atletaId;
    }

    public String getClubeNome() {
        return clubeNome;
    }

    public void setClubeNome(String clubeNome) {
        this.clubeNome = clubeNome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
