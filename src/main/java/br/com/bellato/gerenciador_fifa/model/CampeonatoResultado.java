package br.com.bellato.gerenciador_fifa.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "campeonato_resultado")
public class CampeonatoResultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoResultadoId")
    private Long campeonatoResultadoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeonato_id", nullable = false, unique = true)
    private Campeonato campeonato;

    @Column(name = "campeonatoResultadoNome", nullable = false)
    private String nomeCampeonato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeao_clube_origem_id", nullable = false)
    private Clube campeaoClube;

    @Column(name = "campeonatoResultadoCampeaoCompetidor")
    private Integer campeaoCompetidor;

    @Column(name = "campeonatoResultadoCampeaoCompetidorNome")
    private String campeaoCompetidorNome;

    @ManyToOne
    @JoinColumn(name = "vice_clube_origem_id")
    private Clube viceCampeaoClube;

    @Column(name = "campeonatoResultadoDataConquista", nullable = false)
    private LocalDateTime dataConquista;

    @Column(name = "campeonatoResultadoQuantidadeParticipantes", nullable = false)
    private Integer quantidadeParticipantes;

    @Column(name = "campeonatoResultadoFormato", nullable = false)
    private Integer formato;

    public Long getCampeonatoResultadoId() {
        return campeonatoResultadoId;
    }

    public void setCampeonatoResultadoId(Long campeonatoResultadoId) {
        this.campeonatoResultadoId = campeonatoResultadoId;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public String getNomeCampeonato() {
        return nomeCampeonato;
    }

    public void setNomeCampeonato(String nomeCampeonato) {
        this.nomeCampeonato = nomeCampeonato;
    }

    public Clube getCampeaoClube() {
        return campeaoClube;
    }

    public void setCampeaoClube(Clube campeaoClube) {
        this.campeaoClube = campeaoClube;
    }

    public Integer getCampeaoCompetidor() {
        return campeaoCompetidor;
    }

    public void setCampeaoCompetidor(Integer campeaoCompetidor) {
        this.campeaoCompetidor = campeaoCompetidor;
    }

    public String getCampeaoCompetidorNome() {
        return campeaoCompetidorNome;
    }

    public void setCampeaoCompetidorNome(String campeaoCompetidorNome) {
        this.campeaoCompetidorNome = campeaoCompetidorNome;
    }

    public Clube getViceCampeaoClube() {
        return viceCampeaoClube;
    }

    public void setViceCampeaoClube(Clube viceCampeaoClube) {
        this.viceCampeaoClube = viceCampeaoClube;
    }

    public LocalDateTime getDataConquista() {
        return dataConquista;
    }

    public void setDataConquista(LocalDateTime dataConquista) {
        this.dataConquista = dataConquista;
    }

    public Integer getQuantidadeParticipantes() {
        return quantidadeParticipantes;
    }

    public void setQuantidadeParticipantes(Integer quantidadeParticipantes) {
        this.quantidadeParticipantes = quantidadeParticipantes;
    }

    public Integer getFormato() {
        return formato;
    }

    public void setFormato(Integer formato) {
        this.formato = formato;
    }
}
