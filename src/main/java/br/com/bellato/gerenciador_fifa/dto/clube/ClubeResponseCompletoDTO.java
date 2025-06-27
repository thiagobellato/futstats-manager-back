package br.com.bellato.gerenciador_fifa.dto.clube;

public class ClubeResponseCompletoDTO {

    public Long clubeId;
    public String nome;
    public String sigla;
    public String pais;

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

    public ClubeResponseCompletoDTO(Long clubeId, String nome, String sigla, String pais) {
        this.clubeId = clubeId;
        this.nome = nome;
        this.sigla = sigla;
        this.pais = pais;
    }

    public ClubeResponseCompletoDTO() {
    }

    @Override
    public String toString() {
        return "ClubeResponseCompletoDTO [clubeId=" + clubeId + ", nome=" + nome + ", sigla=" + sigla + ", pais=" + pais
                + "]";
    }

}
