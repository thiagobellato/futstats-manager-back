package br.com.bellato.gerenciador_fifa.dto.tatica;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;
import br.com.bellato.gerenciador_fifa.enums.TipoTaticaJogador;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaticaJogadorResponseDTO {

    private Long taticaJogadorId;
    private Long atletaId;
    private String nome;
    private String sobrenome;
    private PosicaoFutebol posicao;
    private String nacionalidade;
    private TipoTaticaJogador tipo;
    private Double posicaoX;
    private Double posicaoY;
    private Integer ordemReserva;

    public Long getTaticaJogadorId() {
        return taticaJogadorId;
    }

    public void setTaticaJogadorId(Long taticaJogadorId) {
        this.taticaJogadorId = taticaJogadorId;
    }

    public Long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
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

    public TipoTaticaJogador getTipo() {
        return tipo;
    }

    public void setTipo(TipoTaticaJogador tipo) {
        this.tipo = tipo;
    }

    public Double getPosicaoX() {
        return posicaoX;
    }

    public void setPosicaoX(Double posicaoX) {
        this.posicaoX = posicaoX;
    }

    public Double getPosicaoY() {
        return posicaoY;
    }

    public void setPosicaoY(Double posicaoY) {
        this.posicaoY = posicaoY;
    }

    public Integer getOrdemReserva() {
        return ordemReserva;
    }

    public void setOrdemReserva(Integer ordemReserva) {
        this.ordemReserva = ordemReserva;
    }
}
