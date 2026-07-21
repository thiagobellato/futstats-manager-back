package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoEstatisticasDTO {

    private List<ClassificacaoClubeDTO> classificacao = new ArrayList<>();
    private List<RankingAtletaCampeonatoDTO> artilharia = new ArrayList<>();
    private List<RankingAtletaCampeonatoDTO> assistencias = new ArrayList<>();
    private List<RankingAtletaCampeonatoDTO> cartoesAmarelos = new ArrayList<>();
    private List<RankingAtletaCampeonatoDTO> cartoesVermelhos = new ArrayList<>();
    private CampeonatoDashboardDTO dashboard;

    public List<ClassificacaoClubeDTO> getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(List<ClassificacaoClubeDTO> classificacao) {
        this.classificacao = classificacao;
    }

    public List<RankingAtletaCampeonatoDTO> getArtilharia() {
        return artilharia;
    }

    public void setArtilharia(List<RankingAtletaCampeonatoDTO> artilharia) {
        this.artilharia = artilharia;
    }

    public List<RankingAtletaCampeonatoDTO> getAssistencias() {
        return assistencias;
    }

    public void setAssistencias(List<RankingAtletaCampeonatoDTO> assistencias) {
        this.assistencias = assistencias;
    }

    public List<RankingAtletaCampeonatoDTO> getCartoesAmarelos() {
        return cartoesAmarelos;
    }

    public void setCartoesAmarelos(List<RankingAtletaCampeonatoDTO> cartoesAmarelos) {
        this.cartoesAmarelos = cartoesAmarelos;
    }

    public List<RankingAtletaCampeonatoDTO> getCartoesVermelhos() {
        return cartoesVermelhos;
    }

    public void setCartoesVermelhos(List<RankingAtletaCampeonatoDTO> cartoesVermelhos) {
        this.cartoesVermelhos = cartoesVermelhos;
    }

    public CampeonatoDashboardDTO getDashboard() {
        return dashboard;
    }

    public void setDashboard(CampeonatoDashboardDTO dashboard) {
        this.dashboard = dashboard;
    }
}
