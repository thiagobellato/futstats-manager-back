package br.com.bellato.gerenciador_fifa.dto.hall;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallRecordesDTO {

    private List<HallRecordeItemDTO> clubesMaisTitulos = new ArrayList<>();
    private List<HallRecordeItemDTO> clubesMaisVices = new ArrayList<>();
    private List<HallRecordeItemDTO> clubesMaisFinais = new ArrayList<>();
    private List<HallRecordeItemDTO> clubesMaiorSequenciaTitulos = new ArrayList<>();
    private List<HallRecordeItemDTO> clubesMaisParticipacoes = new ArrayList<>();
    private List<HallRecordeItemDTO> clubesMelhorAproveitamento = new ArrayList<>();

    private List<HallRecordeItemDTO> competidoresMaisTitulos = new ArrayList<>();
    private List<HallRecordeItemDTO> competidoresMaisFinais = new ArrayList<>();
    private List<HallRecordeItemDTO> competidoresMaisVices = new ArrayList<>();
    private List<HallRecordeItemDTO> competidoresMelhorAproveitamento = new ArrayList<>();
    private List<HallRecordeItemDTO> competidoresMaisVitorias = new ArrayList<>();
    private List<HallRecordeItemDTO> competidoresMaisPartidas = new ArrayList<>();

    private List<HallRecordeItemDTO> jogadoresMaisGols = new ArrayList<>();
    private List<HallRecordeItemDTO> jogadoresMaisAssistencias = new ArrayList<>();
    private List<HallRecordeItemDTO> jogadoresMaisGolsContra = new ArrayList<>();
    private List<HallRecordeItemDTO> jogadoresMaisAmarelos = new ArrayList<>();
    private List<HallRecordeItemDTO> jogadoresMaisVermelhos = new ArrayList<>();
    private List<HallRecordeItemDTO> jogadoresMaisTitulos = new ArrayList<>();

    public List<HallRecordeItemDTO> getClubesMaisTitulos() {
        return clubesMaisTitulos;
    }

    public void setClubesMaisTitulos(List<HallRecordeItemDTO> clubesMaisTitulos) {
        this.clubesMaisTitulos = clubesMaisTitulos;
    }

    public List<HallRecordeItemDTO> getClubesMaisVices() {
        return clubesMaisVices;
    }

    public void setClubesMaisVices(List<HallRecordeItemDTO> clubesMaisVices) {
        this.clubesMaisVices = clubesMaisVices;
    }

    public List<HallRecordeItemDTO> getClubesMaisFinais() {
        return clubesMaisFinais;
    }

    public void setClubesMaisFinais(List<HallRecordeItemDTO> clubesMaisFinais) {
        this.clubesMaisFinais = clubesMaisFinais;
    }

    public List<HallRecordeItemDTO> getClubesMaiorSequenciaTitulos() {
        return clubesMaiorSequenciaTitulos;
    }

    public void setClubesMaiorSequenciaTitulos(List<HallRecordeItemDTO> clubesMaiorSequenciaTitulos) {
        this.clubesMaiorSequenciaTitulos = clubesMaiorSequenciaTitulos;
    }

    public List<HallRecordeItemDTO> getClubesMaisParticipacoes() {
        return clubesMaisParticipacoes;
    }

    public void setClubesMaisParticipacoes(List<HallRecordeItemDTO> clubesMaisParticipacoes) {
        this.clubesMaisParticipacoes = clubesMaisParticipacoes;
    }

    public List<HallRecordeItemDTO> getClubesMelhorAproveitamento() {
        return clubesMelhorAproveitamento;
    }

    public void setClubesMelhorAproveitamento(List<HallRecordeItemDTO> clubesMelhorAproveitamento) {
        this.clubesMelhorAproveitamento = clubesMelhorAproveitamento;
    }

    public List<HallRecordeItemDTO> getCompetidoresMaisTitulos() {
        return competidoresMaisTitulos;
    }

    public void setCompetidoresMaisTitulos(List<HallRecordeItemDTO> competidoresMaisTitulos) {
        this.competidoresMaisTitulos = competidoresMaisTitulos;
    }

    public List<HallRecordeItemDTO> getCompetidoresMaisFinais() {
        return competidoresMaisFinais;
    }

    public void setCompetidoresMaisFinais(List<HallRecordeItemDTO> competidoresMaisFinais) {
        this.competidoresMaisFinais = competidoresMaisFinais;
    }

    public List<HallRecordeItemDTO> getCompetidoresMaisVices() {
        return competidoresMaisVices;
    }

    public void setCompetidoresMaisVices(List<HallRecordeItemDTO> competidoresMaisVices) {
        this.competidoresMaisVices = competidoresMaisVices;
    }

    public List<HallRecordeItemDTO> getCompetidoresMelhorAproveitamento() {
        return competidoresMelhorAproveitamento;
    }

    public void setCompetidoresMelhorAproveitamento(List<HallRecordeItemDTO> competidoresMelhorAproveitamento) {
        this.competidoresMelhorAproveitamento = competidoresMelhorAproveitamento;
    }

    public List<HallRecordeItemDTO> getCompetidoresMaisVitorias() {
        return competidoresMaisVitorias;
    }

    public void setCompetidoresMaisVitorias(List<HallRecordeItemDTO> competidoresMaisVitorias) {
        this.competidoresMaisVitorias = competidoresMaisVitorias;
    }

    public List<HallRecordeItemDTO> getCompetidoresMaisPartidas() {
        return competidoresMaisPartidas;
    }

    public void setCompetidoresMaisPartidas(List<HallRecordeItemDTO> competidoresMaisPartidas) {
        this.competidoresMaisPartidas = competidoresMaisPartidas;
    }

    public List<HallRecordeItemDTO> getJogadoresMaisGols() {
        return jogadoresMaisGols;
    }

    public void setJogadoresMaisGols(List<HallRecordeItemDTO> jogadoresMaisGols) {
        this.jogadoresMaisGols = jogadoresMaisGols;
    }

    public List<HallRecordeItemDTO> getJogadoresMaisAssistencias() {
        return jogadoresMaisAssistencias;
    }

    public void setJogadoresMaisAssistencias(List<HallRecordeItemDTO> jogadoresMaisAssistencias) {
        this.jogadoresMaisAssistencias = jogadoresMaisAssistencias;
    }

    public List<HallRecordeItemDTO> getJogadoresMaisGolsContra() {
        return jogadoresMaisGolsContra;
    }

    public void setJogadoresMaisGolsContra(List<HallRecordeItemDTO> jogadoresMaisGolsContra) {
        this.jogadoresMaisGolsContra = jogadoresMaisGolsContra;
    }

    public List<HallRecordeItemDTO> getJogadoresMaisAmarelos() {
        return jogadoresMaisAmarelos;
    }

    public void setJogadoresMaisAmarelos(List<HallRecordeItemDTO> jogadoresMaisAmarelos) {
        this.jogadoresMaisAmarelos = jogadoresMaisAmarelos;
    }

    public List<HallRecordeItemDTO> getJogadoresMaisVermelhos() {
        return jogadoresMaisVermelhos;
    }

    public void setJogadoresMaisVermelhos(List<HallRecordeItemDTO> jogadoresMaisVermelhos) {
        this.jogadoresMaisVermelhos = jogadoresMaisVermelhos;
    }

    public List<HallRecordeItemDTO> getJogadoresMaisTitulos() {
        return jogadoresMaisTitulos;
    }

    public void setJogadoresMaisTitulos(List<HallRecordeItemDTO> jogadoresMaisTitulos) {
        this.jogadoresMaisTitulos = jogadoresMaisTitulos;
    }
}
