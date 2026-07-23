package br.com.bellato.gerenciador_fifa.dto.hall;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.dto.campeonato.RankingAtletaCampeonatoDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallTimelineItemDTO {

    private Long campeonatoId;
    private String nome;
    private Integer ano;
    private LocalDateTime dataFinalizacao;
    private Integer quantidadeClubes;
    private String campeaoClubeNome;
    private Long campeaoClubeId;
    private String campeaoCompetidorNome;
    private RankingAtletaCampeonatoDTO artilheiro;
    private RankingAtletaCampeonatoDTO liderAssistencias;

    public Long getCampeonatoId() {
        return campeonatoId;
    }

    public void setCampeonatoId(Long campeonatoId) {
        this.campeonatoId = campeonatoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public Integer getQuantidadeClubes() {
        return quantidadeClubes;
    }

    public void setQuantidadeClubes(Integer quantidadeClubes) {
        this.quantidadeClubes = quantidadeClubes;
    }

    public String getCampeaoClubeNome() {
        return campeaoClubeNome;
    }

    public void setCampeaoClubeNome(String campeaoClubeNome) {
        this.campeaoClubeNome = campeaoClubeNome;
    }

    public Long getCampeaoClubeId() {
        return campeaoClubeId;
    }

    public void setCampeaoClubeId(Long campeaoClubeId) {
        this.campeaoClubeId = campeaoClubeId;
    }

    public String getCampeaoCompetidorNome() {
        return campeaoCompetidorNome;
    }

    public void setCampeaoCompetidorNome(String campeaoCompetidorNome) {
        this.campeaoCompetidorNome = campeaoCompetidorNome;
    }

    public RankingAtletaCampeonatoDTO getArtilheiro() {
        return artilheiro;
    }

    public void setArtilheiro(RankingAtletaCampeonatoDTO artilheiro) {
        this.artilheiro = artilheiro;
    }

    public RankingAtletaCampeonatoDTO getLiderAssistencias() {
        return liderAssistencias;
    }

    public void setLiderAssistencias(RankingAtletaCampeonatoDTO liderAssistencias) {
        this.liderAssistencias = liderAssistencias;
    }
}
