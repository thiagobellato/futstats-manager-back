package br.com.bellato.gerenciador_fifa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "historico_atleta_campeonato", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "atleta_id", "campeonato_id" })
})
public class HistoricoAtletaCampeonato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historicoAtletaCampeonatoId")
    private Long historicoAtletaCampeonatoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Atleta atleta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @Column(name = "historicoAtletaGols")
    private Integer gols = 0;

    @Column(name = "historicoAtletaAssistencias")
    private Integer assistencias = 0;

    @Column(name = "historicoAtletaCartoesAmarelos")
    private Integer cartoesAmarelos = 0;

    @Column(name = "historicoAtletaCartoesVermelhos")
    private Integer cartoesVermelhos = 0;

    @Column(name = "historicoAtletaGolsContra")
    private Integer golsContra = 0;

    @Column(name = "historicoAtletaTituloConquistado", nullable = false)
    private Boolean tituloConquistado = Boolean.FALSE;

    @Column(name = "historicoAtletaTransferencias")
    private Integer transferencias = 0;

    @Column(name = "historicoAtletaClubesDefendidos", length = 1000)
    private String clubesDefendidos;

    public Long getHistoricoAtletaCampeonatoId() {
        return historicoAtletaCampeonatoId;
    }

    public void setHistoricoAtletaCampeonatoId(Long historicoAtletaCampeonatoId) {
        this.historicoAtletaCampeonatoId = historicoAtletaCampeonatoId;
    }

    public Atleta getAtleta() {
        return atleta;
    }

    public void setAtleta(Atleta atleta) {
        this.atleta = atleta;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public Integer getGols() {
        return gols;
    }

    public void setGols(Integer gols) {
        this.gols = gols;
    }

    public Integer getAssistencias() {
        return assistencias;
    }

    public void setAssistencias(Integer assistencias) {
        this.assistencias = assistencias;
    }

    public Integer getCartoesAmarelos() {
        return cartoesAmarelos;
    }

    public void setCartoesAmarelos(Integer cartoesAmarelos) {
        this.cartoesAmarelos = cartoesAmarelos;
    }

    public Integer getCartoesVermelhos() {
        return cartoesVermelhos;
    }

    public void setCartoesVermelhos(Integer cartoesVermelhos) {
        this.cartoesVermelhos = cartoesVermelhos;
    }

    public Integer getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(Integer golsContra) {
        this.golsContra = golsContra;
    }

    public Boolean getTituloConquistado() {
        return tituloConquistado;
    }

    public void setTituloConquistado(Boolean tituloConquistado) {
        this.tituloConquistado = tituloConquistado;
    }

    public Integer getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(Integer transferencias) {
        this.transferencias = transferencias;
    }

    public String getClubesDefendidos() {
        return clubesDefendidos;
    }

    public void setClubesDefendidos(String clubesDefendidos) {
        this.clubesDefendidos = clubesDefendidos;
    }
}
