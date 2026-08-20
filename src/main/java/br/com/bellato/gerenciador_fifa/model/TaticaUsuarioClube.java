package br.com.bellato.gerenciador_fifa.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.bellato.gerenciador_fifa.enums.FormacaoTatica;
import br.com.bellato.gerenciador_fifa.enums.FormacaoTatica.FormacaoTaticaConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Configuração tática pessoal vinculada a usuário + clube.
 */
@Entity
@Table(name = "tatica_usuario_clube", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tatica_usuario_clube", columnNames = { "user_id", "clube_id" })
})
public class TaticaUsuarioClube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taticaUsuarioClubeId")
    private Long taticaUsuarioClubeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clube_id", nullable = false)
    private Clube clube;

    @Column(name = "taticaFormacao", nullable = false)
    @Convert(converter = FormacaoTaticaConverter.class)
    private FormacaoTatica formacao = FormacaoTatica.F_4_4_2;

    @Column(name = "taticaAnotacoes", columnDefinition = "TEXT")
    private String anotacoes;

    @Column(name = "capitaoAtletaId")
    private Long capitaoAtletaId;

    @Column(name = "batedorPenaltisAtletaId")
    private Long batedorPenaltisAtletaId;

    @Column(name = "batedorFaltaAtletaId")
    private Long batedorFaltaAtletaId;

    @Column(name = "batedorEscanteioEsquerdoAtletaId")
    private Long batedorEscanteioEsquerdoAtletaId;

    @Column(name = "batedorEscanteioDireitoAtletaId")
    private Long batedorEscanteioDireitoAtletaId;

    @Column(name = "taticaDataUltimaAtualizacao", nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    @OneToMany(mappedBy = "taticaUsuarioClube", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaticaJogador> jogadores = new ArrayList<>();

    public TaticaUsuarioClube() {
    }

    public Long getTaticaUsuarioClubeId() {
        return taticaUsuarioClubeId;
    }

    public void setTaticaUsuarioClubeId(Long taticaUsuarioClubeId) {
        this.taticaUsuarioClubeId = taticaUsuarioClubeId;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Clube getClube() {
        return clube;
    }

    public void setClube(Clube clube) {
        this.clube = clube;
    }

    public FormacaoTatica getFormacao() {
        return formacao;
    }

    public void setFormacao(FormacaoTatica formacao) {
        this.formacao = formacao;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public Long getCapitaoAtletaId() {
        return capitaoAtletaId;
    }

    public void setCapitaoAtletaId(Long capitaoAtletaId) {
        this.capitaoAtletaId = capitaoAtletaId;
    }

    public Long getBatedorPenaltisAtletaId() {
        return batedorPenaltisAtletaId;
    }

    public void setBatedorPenaltisAtletaId(Long batedorPenaltisAtletaId) {
        this.batedorPenaltisAtletaId = batedorPenaltisAtletaId;
    }

    public Long getBatedorFaltaAtletaId() {
        return batedorFaltaAtletaId;
    }

    public void setBatedorFaltaAtletaId(Long batedorFaltaAtletaId) {
        this.batedorFaltaAtletaId = batedorFaltaAtletaId;
    }

    public Long getBatedorEscanteioEsquerdoAtletaId() {
        return batedorEscanteioEsquerdoAtletaId;
    }

    public void setBatedorEscanteioEsquerdoAtletaId(Long batedorEscanteioEsquerdoAtletaId) {
        this.batedorEscanteioEsquerdoAtletaId = batedorEscanteioEsquerdoAtletaId;
    }

    public Long getBatedorEscanteioDireitoAtletaId() {
        return batedorEscanteioDireitoAtletaId;
    }

    public void setBatedorEscanteioDireitoAtletaId(Long batedorEscanteioDireitoAtletaId) {
        this.batedorEscanteioDireitoAtletaId = batedorEscanteioDireitoAtletaId;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<TaticaJogador> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<TaticaJogador> jogadores) {
        this.jogadores = jogadores;
    }
}
