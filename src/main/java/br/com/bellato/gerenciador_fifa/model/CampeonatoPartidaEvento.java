package br.com.bellato.gerenciador_fifa.model;

import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida;
import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida.TipoEventoPartidaConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "campeonato_partida_evento")
public class CampeonatoPartidaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoPartidaEventoId")
    private Long campeonatoPartidaEventoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeonato_partida_id", nullable = false)
    private CampeonatoPartida partida;

    @Column(name = "campeonatoPartidaEventoTipo", nullable = false)
    @Convert(converter = TipoEventoPartidaConverter.class)
    private TipoEventoPartida tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeonato_atleta_id", nullable = false)
    private CampeonatoAtleta atleta;

    @Column(name = "campeonatoPartidaEventoOrdem")
    private Integer ordem;

    public CampeonatoPartidaEvento() {
    }

    public Long getCampeonatoPartidaEventoId() {
        return campeonatoPartidaEventoId;
    }

    public void setCampeonatoPartidaEventoId(Long campeonatoPartidaEventoId) {
        this.campeonatoPartidaEventoId = campeonatoPartidaEventoId;
    }

    public CampeonatoPartida getPartida() {
        return partida;
    }

    public void setPartida(CampeonatoPartida partida) {
        this.partida = partida;
    }

    public TipoEventoPartida getTipo() {
        return tipo;
    }

    public void setTipo(TipoEventoPartida tipo) {
        this.tipo = tipo;
    }

    public CampeonatoAtleta getAtleta() {
        return atleta;
    }

    public void setAtleta(CampeonatoAtleta atleta) {
        this.atleta = atleta;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
