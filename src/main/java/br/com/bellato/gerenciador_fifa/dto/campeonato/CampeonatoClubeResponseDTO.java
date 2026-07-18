package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoClubeResponseDTO {

    private Long campeonatoClubeId;
    private Long clubeOrigemId;
    private String nome;
    private String sigla;
    private String pais;
    private ClubRank rank;
    private Integer competidorNumero;
    private Boolean campeaoAnterior;
    private Boolean excluidoSorteio;
    private Long quantidadeAtletas;

    public Long getCampeonatoClubeId() {
        return campeonatoClubeId;
    }

    public void setCampeonatoClubeId(Long campeonatoClubeId) {
        this.campeonatoClubeId = campeonatoClubeId;
    }

    public Long getClubeOrigemId() {
        return clubeOrigemId;
    }

    public void setClubeOrigemId(Long clubeOrigemId) {
        this.clubeOrigemId = clubeOrigemId;
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

    public Integer getCompetidorNumero() {
        return competidorNumero;
    }

    public void setCompetidorNumero(Integer competidorNumero) {
        this.competidorNumero = competidorNumero;
    }

    public Boolean getCampeaoAnterior() {
        return campeaoAnterior;
    }

    public void setCampeaoAnterior(Boolean campeaoAnterior) {
        this.campeaoAnterior = campeaoAnterior;
    }

    public Boolean getExcluidoSorteio() {
        return excluidoSorteio;
    }

    public void setExcluidoSorteio(Boolean excluidoSorteio) {
        this.excluidoSorteio = excluidoSorteio;
    }

    public Long getQuantidadeAtletas() {
        return quantidadeAtletas;
    }

    public void setQuantidadeAtletas(Long quantidadeAtletas) {
        this.quantidadeAtletas = quantidadeAtletas;
    }
}
