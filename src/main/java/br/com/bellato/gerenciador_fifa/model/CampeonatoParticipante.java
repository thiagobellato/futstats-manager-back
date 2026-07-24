package br.com.bellato.gerenciador_fifa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Camada intermediária entre campeonato e identidade do competidor.
 * Campeonato livre: user = null, authenticated = false.
 * Campeonato autenticado: user preenchido, authenticated = true.
 * Não substitui competidor1Nome/competidor2Nome (espelho operacional).
 */
@Entity
@Table(name = "campeonato_participante", uniqueConstraints = {
        @UniqueConstraint(name = "uk_camp_participante_lado", columnNames = { "campeonato_id", "side" })
})
public class CampeonatoParticipante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoParticipanteId")
    private Long campeonatoParticipanteId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "displayName", nullable = false, length = 80)
    private String displayName;

    @Column(name = "authenticated", nullable = false)
    private boolean authenticated;

    /** Lado operacional: 1 ou 2 (equivalente HOME/AWAY). */
    @Column(name = "side", nullable = false)
    private Integer side;

    public CampeonatoParticipante() {
    }

    public Long getCampeonatoParticipanteId() {
        return campeonatoParticipanteId;
    }

    public void setCampeonatoParticipanteId(Long campeonatoParticipanteId) {
        this.campeonatoParticipanteId = campeonatoParticipanteId;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Integer getSide() {
        return side;
    }

    public void setSide(Integer side) {
        this.side = side;
    }
}
