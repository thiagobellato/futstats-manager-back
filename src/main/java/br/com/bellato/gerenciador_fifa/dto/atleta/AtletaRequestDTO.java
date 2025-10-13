package br.com.bellato.gerenciador_fifa.dto.atleta;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AtletaRequestDTO {

    private Long clubeId;
    private String nome;
    private PosicaoFutebol posicao;
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
    public PosicaoFutebol getPosicao() {
        return posicao;
    }
    public void setPosicao(PosicaoFutebol posicao) {
        this.posicao = posicao;
    }

    

}