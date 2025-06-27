package br.com.bellato.gerenciador_fifa.dto.atleta;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AtletaResponseCompletoDTO {

    private long atletaId;
    private String clubeNome;
    private String nome;
    private String sobrenome;
    private PosicaoFutebol posicao;
    private String nacionalidade;

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

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public PosicaoFutebol getPosicao() {
        return posicao;
    }

    public void setPosicao(PosicaoFutebol posicao) {
        this.posicao = posicao;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

}
