package br.com.bellato.gerenciador_fifa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Suspensão automática por cartão vermelho, escopo exclusivo do campeonato.
 * Vinculada à identidade do atleta para sobreviver a transferências internas.
 */
@Entity
@Table(name = "campeonato_suspensao")
public class CampeonatoSuspensao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoSuspensaoId")
    private Long campeonatoSuspensaoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @Column(name = "campeonatoSuspensaoIdentidade", nullable = false, length = 64)
    private String identidade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_origem_id", nullable = false)
    private CampeonatoPartida partidaOrigem;

    @ManyToOne
    @JoinColumn(name = "partida_cumprimento_id")
    private CampeonatoPartida partidaCumprimento;

    @Column(name = "campeonatoSuspensaoAtiva", nullable = false)
    private Boolean ativa = Boolean.TRUE;

    public CampeonatoSuspensao() {
    }

    public Long getCampeonatoSuspensaoId() {
        return campeonatoSuspensaoId;
    }

    public void setCampeonatoSuspensaoId(Long campeonatoSuspensaoId) {
        this.campeonatoSuspensaoId = campeonatoSuspensaoId;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public String getIdentidade() {
        return identidade;
    }

    public void setIdentidade(String identidade) {
        this.identidade = identidade;
    }

    public CampeonatoPartida getPartidaOrigem() {
        return partidaOrigem;
    }

    public void setPartidaOrigem(CampeonatoPartida partidaOrigem) {
        this.partidaOrigem = partidaOrigem;
    }

    public CampeonatoPartida getPartidaCumprimento() {
        return partidaCumprimento;
    }

    public void setPartidaCumprimento(CampeonatoPartida partidaCumprimento) {
        this.partidaCumprimento = partidaCumprimento;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public boolean isAtiva() {
        return Boolean.TRUE.equals(ativa);
    }
}
