package br.com.bellato.gerenciador_fifa.dto.atleta;

public class AtletaRequestDTO {

    private String nome;
    private Long clubeId;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getClubeId() {
        return clubeId;
    }

    public void setClubeId(Long clubeId) {
        this.clubeId = clubeId;
    }

}