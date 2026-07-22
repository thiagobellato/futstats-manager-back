package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoFinalizacaoResponseDTO {

    private Long campeonatoId;
    private String nome;
    private String status;
    private LocalDateTime dataFinalizacao;
    private String campeaoClubeNome;
    private String viceCampeaoClubeNome;
    private Integer formato;
    private int atletasSincronizados;
    private int atletasCriadosGlobalmente;
    private int clubesAtualizados;
    private int ranksAlterados;
    private List<String> avisos = new ArrayList<>();

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public String getCampeaoClubeNome() {
        return campeaoClubeNome;
    }

    public void setCampeaoClubeNome(String campeaoClubeNome) {
        this.campeaoClubeNome = campeaoClubeNome;
    }

    public String getViceCampeaoClubeNome() {
        return viceCampeaoClubeNome;
    }

    public void setViceCampeaoClubeNome(String viceCampeaoClubeNome) {
        this.viceCampeaoClubeNome = viceCampeaoClubeNome;
    }

    public Integer getFormato() {
        return formato;
    }

    public void setFormato(Integer formato) {
        this.formato = formato;
    }

    public int getAtletasSincronizados() {
        return atletasSincronizados;
    }

    public void setAtletasSincronizados(int atletasSincronizados) {
        this.atletasSincronizados = atletasSincronizados;
    }

    public int getAtletasCriadosGlobalmente() {
        return atletasCriadosGlobalmente;
    }

    public void setAtletasCriadosGlobalmente(int atletasCriadosGlobalmente) {
        this.atletasCriadosGlobalmente = atletasCriadosGlobalmente;
    }

    public int getClubesAtualizados() {
        return clubesAtualizados;
    }

    public void setClubesAtualizados(int clubesAtualizados) {
        this.clubesAtualizados = clubesAtualizados;
    }

    public int getRanksAlterados() {
        return ranksAlterados;
    }

    public void setRanksAlterados(int ranksAlterados) {
        this.ranksAlterados = ranksAlterados;
    }

    public List<String> getAvisos() {
        return avisos;
    }

    public void setAvisos(List<String> avisos) {
        this.avisos = avisos;
    }
}
