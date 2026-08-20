package br.com.bellato.gerenciador_fifa.dto.tatica;

import br.com.bellato.gerenciador_fifa.enums.TipoTaticaJogador;

public class TaticaJogadorRequestDTO {

    private Long atletaId;
    private TipoTaticaJogador tipo;
    private Double posicaoX;
    private Double posicaoY;
    private Integer ordemReserva;

    public Long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
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
