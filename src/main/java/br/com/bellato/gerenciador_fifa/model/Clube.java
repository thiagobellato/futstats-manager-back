package br.com.bellato.gerenciador_fifa.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

/**
 * Dados cadastrais do clube. Desempenho esportivo (rank, V/E/D, gols, títulos)
 * fica em {@link EstatisticaClube}.
 */
@Entity
@Table(name = "clube")
public class Clube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clubeId")
    private long clubeId;

    @Column(name = "clubeNome")
    private String nome;

    @Column(name = "clubeSigla")
    private String sigla;

    @Column(name = "clubePais")
    private String pais;

    @OneToOne(mappedBy = "clube", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EstatisticaClube estatistica;

    @OneToMany(mappedBy = "clube", cascade = CascadeType.ALL)
    private List<Atleta> atletas;

    public Clube() {
    }

    public Clube(long clubeId, String nome, String sigla, String pais) {
        this.clubeId = clubeId;
        this.nome = nome;
        this.sigla = sigla;
        this.pais = pais;
    }

    public long getClubeId() {
        return clubeId;
    }

    public void setClubeId(long clubeId) {
        this.clubeId = clubeId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public EstatisticaClube getEstatistica() {
        return estatistica;
    }

    public void setEstatistica(EstatisticaClube estatistica) {
        this.estatistica = estatistica;
        if (estatistica != null) {
            estatistica.setClube(this);
        }
    }

    /**
     * Atalho transparente para o rank persistido em {@link EstatisticaClube}.
     * Não é coluna de {@code clube}.
     */
    @Transient
    public ClubRank getRank() {
        return estatistica != null ? estatistica.getRank() : null;
    }

    /**
     * Atalho transparente: garante estatística e grava o rank nela.
     */
    public void setRank(ClubRank rank) {
        if (estatistica == null) {
            setEstatistica(EstatisticaClube.zerada(this));
        }
        estatistica.setRank(rank);
    }

    @Override
    public String toString() {
        return "Clube [clubeId=" + clubeId + ", nome=" + nome + ", sigla=" + sigla + ", pais=" + pais + "]";
    }

}
