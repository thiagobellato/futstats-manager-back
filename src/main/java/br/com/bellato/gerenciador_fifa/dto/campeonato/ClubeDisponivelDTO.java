package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubeDisponivelDTO {

    private Long clubeId;
    private String nome;
    private String sigla;
    private String pais;
    private ClubRank rank;
    private Boolean protegido;
    private Boolean elegivelParaPote;

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

    public Boolean getProtegido() {
        return protegido;
    }

    public void setProtegido(Boolean protegido) {
        this.protegido = protegido;
    }

    public Boolean getElegivelParaPote() {
        return elegivelParaPote;
    }

    public void setElegivelParaPote(Boolean elegivelParaPote) {
        this.elegivelParaPote = elegivelParaPote;
    }
}
