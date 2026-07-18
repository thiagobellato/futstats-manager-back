package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubeDisponivelDTO {

    private Long clubeId;
    private String nome;
    private String sigla;
    private String pais;
    private ClubRank rank;

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
