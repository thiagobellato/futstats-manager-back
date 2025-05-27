package br.com.bellato.gerenciador_fifa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
    private char sigla;

    @Column(name = "clubePais")
    private String pais;

    public Clube() {
    }

    public Clube(long clubeId, String nome, char sigla, String pais) {
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

    public char getSigla() {
        return sigla;
    }

    public void setSigla(char sigla) {
        this.sigla = sigla;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
        return "Clube [clubeId=" + clubeId + ", nome=" + nome + ", sigla=" + sigla + ", pais=" + pais + "]";
    }

}
