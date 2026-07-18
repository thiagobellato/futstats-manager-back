package br.com.bellato.gerenciador_fifa.dto.clube;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubeRequestDTO {

    private String nome;
    private String sigla;
    private String pais;
    private ClubRank rank;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public ClubRank getRank() {
        return rank;
    }

    public void setRank(ClubRank rank) {
        this.rank = rank;
    }

}
