package br.com.bellato.gerenciador_fifa.dto.clube;

public class ClubeResponseDTO {

    private long clubeId;
    private String nome;

    public long getClubeId() {
        return clubeId;
    }

    public void setClubeId(long clubeId) {
        this.clubeId = clubeId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
