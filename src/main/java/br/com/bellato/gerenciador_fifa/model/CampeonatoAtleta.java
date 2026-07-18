package br.com.bellato.gerenciador_fifa.model;

import java.time.LocalDate;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;
import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol.PosicaoFutebolConverter;
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
@Table(name = "campeonato_atleta")
public class CampeonatoAtleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoAtletaId")
    private Long campeonatoAtletaId;

    @ManyToOne
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @ManyToOne
    @JoinColumn(name = "campeonato_clube_id", nullable = false)
    private CampeonatoClube campeonatoClube;

    @Column(name = "campeonatoAtletaOrigemId", nullable = false)
    private Long atletaOrigemId;

    @Column(name = "campeonatoAtletaNome", nullable = false)
    private String nome;

    @Column(name = "campeonatoAtletaSobrenome")
    private String sobrenome;

    @Column(name = "campeonatoAtletaDataDeNascimento")
    private LocalDate dataDeNascimento;

    @Column(name = "campeonatoAtletaNacionalidade")
    private String nacionalidade;

    @Column(name = "campeonatoAtletaPosicao")
    @Convert(converter = PosicaoFutebolConverter.class)
    private PosicaoFutebol posicao;

    @Column(name = "campeonatoAtletaGols")
    private Integer gols = 0;

    @Column(name = "campeonatoAtletaAssistencias")
    private Integer assistencias = 0;

    @Column(name = "campeonatoAtletaCartoesAmarelos")
    private Integer cartoesAmarelos = 0;

    @Column(name = "campeonatoAtletaCartoesVermelhos")
    private Integer cartoesVermelhos = 0;

    public CampeonatoAtleta() {
    }

    public Long getCampeonatoAtletaId() {
        return campeonatoAtletaId;
    }

    public void setCampeonatoAtletaId(Long campeonatoAtletaId) {
        this.campeonatoAtletaId = campeonatoAtletaId;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public CampeonatoClube getCampeonatoClube() {
        return campeonatoClube;
    }

    public void setCampeonatoClube(CampeonatoClube campeonatoClube) {
        this.campeonatoClube = campeonatoClube;
    }

    public Long getAtletaOrigemId() {
        return atletaOrigemId;
    }

    public void setAtletaOrigemId(Long atletaOrigemId) {
        this.atletaOrigemId = atletaOrigemId;
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
}
