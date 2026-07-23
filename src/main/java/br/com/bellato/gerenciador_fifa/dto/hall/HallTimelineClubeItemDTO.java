package br.com.bellato.gerenciador_fifa.dto.hall;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallTimelineClubeItemDTO {

    private Long campeonatoId;
    private String campeonatoNome;
    private Integer ano;
    private LocalDateTime dataFinalizacao;
    private Integer posicaoFinal;
    private Boolean tituloConquistado;
    private Boolean vice;
    private String rankInicial;
    private String rankFinal;
    private String evolucaoRank;
    private Integer jogos;
    private Integer vitorias;
    private Integer empates;
    private Integer derrotas;
    private Integer golsPro;
    private Integer golsContra;
    private Integer pontos;
    private Integer saldoGols;

    public Long getCampeonatoId() {
        return campeonatoId;
    }

    public void setCampeonatoId(Long campeonatoId) {
        this.campeonatoId = campeonatoId;
    }

    public String getCampeonatoNome() {
        return campeonatoNome;
    }

    public void setCampeonatoNome(String campeonatoNome) {
        this.campeonatoNome = campeonatoNome;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public Integer getPosicaoFinal() {
        return posicaoFinal;
    }

    public void setPosicaoFinal(Integer posicaoFinal) {
        this.posicaoFinal = posicaoFinal;
    }

    public Boolean getTituloConquistado() {
        return tituloConquistado;
    }

    public void setTituloConquistado(Boolean tituloConquistado) {
        this.tituloConquistado = tituloConquistado;
    }

    public Boolean getVice() {
        return vice;
    }

    public void setVice(Boolean vice) {
        this.vice = vice;
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

    public Integer getJogos() {
        return jogos;
    }

    public void setJogos(Integer jogos) {
        this.jogos = jogos;
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

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }

    public Integer getSaldoGols() {
        return saldoGols;
    }

    public void setSaldoGols(Integer saldoGols) {
        this.saldoGols = saldoGols;
    }
}
