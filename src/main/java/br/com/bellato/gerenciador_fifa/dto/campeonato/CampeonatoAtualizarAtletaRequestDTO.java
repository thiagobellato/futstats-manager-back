package br.com.bellato.gerenciador_fifa.dto.campeonato;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;

public class CampeonatoAtualizarAtletaRequestDTO {

    private String nome;
    private String sobrenome;
    private String nacionalidade;
    private PosicaoFutebol posicao;

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

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public PosicaoFutebol getPosicao() {
        return posicao;
    }

    public void setPosicao(PosicaoFutebol posicao) {
        this.posicao = posicao;
    }
}
