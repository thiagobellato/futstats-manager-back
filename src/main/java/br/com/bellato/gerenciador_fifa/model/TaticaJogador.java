package br.com.bellato.gerenciador_fifa.model;

import br.com.bellato.gerenciador_fifa.enums.TipoTaticaJogador;
import br.com.bellato.gerenciador_fifa.enums.TipoTaticaJogador.TipoTaticaJogadorConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tatica_jogador", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tatica_jogador_atleta", columnNames = { "tatica_usuario_clube_id", "atleta_id" })
})
public class TaticaJogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taticaJogadorId")
    private Long taticaJogadorId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tatica_usuario_clube_id", nullable = false)
    private TaticaUsuarioClube taticaUsuarioClube;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Atleta atleta;

    @Column(name = "taticaJogadorTipo", nullable = false)
    @Convert(converter = TipoTaticaJogadorConverter.class)
    private TipoTaticaJogador tipo;

    @Column(name = "taticaJogadorPosicaoX")
    private Double posicaoX;

    @Column(name = "taticaJogadorPosicaoY")
    private Double posicaoY;

    @Column(name = "taticaJogadorOrdemReserva")
    private Integer ordemReserva;

    public TaticaJogador() {
    }

    public Long getTaticaJogadorId() {
        return taticaJogadorId;
    }

    public void setTaticaJogadorId(Long taticaJogadorId) {
        this.taticaJogadorId = taticaJogadorId;
    }

    public TaticaUsuarioClube getTaticaUsuarioClube() {
        return taticaUsuarioClube;
    }

    public void setTaticaUsuarioClube(TaticaUsuarioClube taticaUsuarioClube) {
        this.taticaUsuarioClube = taticaUsuarioClube;
    }

    public Atleta getAtleta() {
        return atleta;
    }

    public void setAtleta(Atleta atleta) {
        this.atleta = atleta;
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
