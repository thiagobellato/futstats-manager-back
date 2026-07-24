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
 * Uso de clube por usuário em campeonatos autenticados (atualização incremental).
 */
@Entity
@Table(name = "uso_clube_usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_uso_clube_usuario", columnNames = { "user_id", "clube_id" })
})
public class UsoClubeUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userClubUsageId")
    private Long usoClubeUsuarioId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clube_id", nullable = false)
    private Clube clube;

    @Column(name = "timesUsed", nullable = false)
    private int vezesUsado;

    @Column(name = "titles", nullable = false)
    private int titulos;

    public UsoClubeUsuario() {
    }

    public Long getUsoClubeUsuarioId() {
        return usoClubeUsuarioId;
    }

    public void setUsoClubeUsuarioId(Long usoClubeUsuarioId) {
        this.usoClubeUsuarioId = usoClubeUsuarioId;
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

    public int getVezesUsado() {
        return vezesUsado;
    }

    public void setVezesUsado(int vezesUsado) {
        this.vezesUsado = vezesUsado;
    }

    public int getTitulos() {
        return titulos;
    }

    public void setTitulos(int titulos) {
        this.titulos = titulos;
    }
}
