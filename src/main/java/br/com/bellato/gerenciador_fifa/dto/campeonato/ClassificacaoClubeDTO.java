package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassificacaoClubeDTO {

    private Long campeonatoClubeId;
    private Long clubeOrigemId;
    private String nome;
    private String sigla;
    private Integer competidorNumero;
    private Boolean eliminado;
    private Integer jogos;
    private Integer pontos;
    private Integer vitorias;
    private Integer empates;
    private Integer derrotas;
    private Integer golsPro;
    private Integer golsContra;
    private Integer saldoGols;
    private String rankInicial;
    private String rankFinal;
    private String evolucaoRank;

    public Long getCampeonatoClubeId() {
        return campeonatoClubeId;
    }

    public void setCampeonatoClubeId(Long campeonatoClubeId) {
        this.campeonatoClubeId = campeonatoClubeId;
    }

    public Long getClubeOrigemId() {
        return clubeOrigemId;
    }

    public void setClubeOrigemId(Long clubeOrigemId) {
        this.clubeOrigemId = clubeOrigemId;
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

    public Integer getCompetidorNumero() {
        return competidorNumero;
    }

    public void setCompetidorNumero(Integer competidorNumero) {
        this.competidorNumero = competidorNumero;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Integer getJogos() {
        return jogos;
    }

    public void setJogos(Integer jogos) {
        this.jogos = jogos;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }

    public Integer getVitorias() {
        return vitorias;
    }

    public void setVitorias(Integer vitorias) {
        this.vitorias = vitorias;
    }

    public Integer getEmpates() {
        return empates;
    }

    public void setEmpates(Integer empates) {
        this.empates = empates;
    }

    public Integer getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(Integer derrotas) {
        this.derrotas = derrotas;
    }

    public Integer getGolsPro() {
        return golsPro;
    }

    public void setGolsPro(Integer golsPro) {
        this.golsPro = golsPro;
    }

    public Integer getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(Integer golsContra) {
        this.golsContra = golsContra;
    }

    public Integer getSaldoGols() {
        return saldoGols;
    }

    public void setSaldoGols(Integer saldoGols) {
        this.saldoGols = saldoGols;
    }

    public String getRankInicial() {
        return rankInicial;
    }

    public void setRankInicial(String rankInicial) {
        this.rankInicial = rankInicial;
    }

    public String getRankFinal() {
        return rankFinal;
    }

    public void setRankFinal(String rankFinal) {
        this.rankFinal = rankFinal;
    }

    public String getEvolucaoRank() {
        return evolucaoRank;
    }

    public void setEvolucaoRank(String evolucaoRank) {
        this.evolucaoRank = evolucaoRank;
    }
}
