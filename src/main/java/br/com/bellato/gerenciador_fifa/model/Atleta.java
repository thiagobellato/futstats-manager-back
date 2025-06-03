package br.com.bellato.gerenciador_fifa.model;

import java.time.LocalDate;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;
import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol.PosicaoFutebolConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;/*  */
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "atleta")
public class Atleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atletaId")
    private Long atletaId;

    @Column(name = "atletaNome")
    private String nome;

    @Column(name = "atletaSobrenome")
    private String sobrenome;

    @Column(name = "atletaDataDeNascimento")
    private LocalDate dataDeNascimento;

    @Column(name = "atletaNacionalidade")
    private String nacionalidade;

    @Column(name = "atletaPosicao")
    @Convert(converter = PosicaoFutebolConverter.class)
    private PosicaoFutebol posicao;

    @Column(name = "atletaGol")
    private int gol = 0;

    @Column(name = "atletaAssistencia")
    private int assistencia = 0;

    @ManyToOne
    @JoinColumn(name = "clube_id")
    private Clube clube;

    public Atleta() {
    }

    public Atleta(Long atletaId, String nome, String sobrenome, LocalDate dataDeNascimento, String nacionalidade,
            PosicaoFutebol posicao, int gol, int assistencia, Clube clube) {
        this.atletaId = atletaId;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.dataDeNascimento = dataDeNascimento;
        this.nacionalidade = nacionalidade;
        this.posicao = posicao;
        this.gol = gol;
        this.assistencia = assistencia;
        this.clube = clube;
    }

    public Long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public LocalDate getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(LocalDate dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public PosicaoFutebol getPosicao() {
        return posicao;
    }

    public void setPosicao(PosicaoFutebol posicao) {
        this.posicao = posicao;
    }

    public int getGol() {
        return gol;
    }

    public void setGol(int gol) {
        this.gol = gol;
    }

    public int getAssistencia() {
        return assistencia;
    }

    public void setAssistencia(int assistencia) {
        this.assistencia = assistencia;
    }

    public Clube getClube() {
        return clube;
    }

    public void setClube(Clube clube) {
        this.clube = clube;
    }

    @Override
    public String toString() {
        return "Atleta [atletaId=" + atletaId + ", nome=" + nome + ", sobrenome=" + sobrenome + ", dataDeNascimento="
                + dataDeNascimento + ", nacionalidade=" + nacionalidade + ", posicao=" + posicao + ", gol=" + gol
                + ", assistencia=" + assistencia + ", clube=" + clube + "]";
    }

   
}
