package br.com.bellato.gerenciador_fifa.dto.atleta;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;

public class AtletaResponseDTO {

    private long atletaId;
    private String clubeNome;
    private String nome;
    private PosicaoFutebol posicao;

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

    public PosicaoFutebol getPosicao() {
        return posicao;
    }

    public void setPosicao(PosicaoFutebol posicao) {
        this.posicao = posicao;
    }

}
