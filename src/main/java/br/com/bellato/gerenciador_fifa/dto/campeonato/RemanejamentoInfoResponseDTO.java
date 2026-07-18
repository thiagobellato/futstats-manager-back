package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemanejamentoInfoResponseDTO {

    private Boolean necessario;
    private Integer competidorVantagem;
    private String competidorVantagemNome;
    private Integer competidorMinoria;
    private String competidorMinoriaNome;
    private Integer quantidadeConfrontos;
    private List<CampeonatoClubeResponseDTO> clubesVantagem;
    private List<CampeonatoClubeResponseDTO> clubesMinoria;
    private List<CampeonatoClubeResponseDTO> clubesBye;

    public Boolean getNecessario() {
        return necessario;
    }

    public void setNecessario(Boolean necessario) {
        this.necessario = necessario;
    }

    public Integer getCompetidorVantagem() {
        return competidorVantagem;
    }

    public void setCompetidorVantagem(Integer competidorVantagem) {
        this.competidorVantagem = competidorVantagem;
    }

    public String getCompetidorVantagemNome() {
        return competidorVantagemNome;
    }

    public void setCompetidorVantagemNome(String competidorVantagemNome) {
        this.competidorVantagemNome = competidorVantagemNome;
    }

    public Integer getCompetidorMinoria() {
        return competidorMinoria;
    }

    public void setCompetidorMinoria(Integer competidorMinoria) {
        this.competidorMinoria = competidorMinoria;
    }

    public String getCompetidorMinoriaNome() {
        return competidorMinoriaNome;
    }

    public void setCompetidorMinoriaNome(String competidorMinoriaNome) {
        this.competidorMinoriaNome = competidorMinoriaNome;
    }

    public Integer getQuantidadeConfrontos() {
        return quantidadeConfrontos;
    }

    public void setQuantidadeConfrontos(Integer quantidadeConfrontos) {
        this.quantidadeConfrontos = quantidadeConfrontos;
    }

    public List<CampeonatoClubeResponseDTO> getClubesVantagem() {
        return clubesVantagem;
    }

    public void setClubesVantagem(List<CampeonatoClubeResponseDTO> clubesVantagem) {
        this.clubesVantagem = clubesVantagem;
    }

    public List<CampeonatoClubeResponseDTO> getClubesMinoria() {
        return clubesMinoria;
    }

    public void setClubesMinoria(List<CampeonatoClubeResponseDTO> clubesMinoria) {
        this.clubesMinoria = clubesMinoria;
    }

    public List<CampeonatoClubeResponseDTO> getClubesBye() {
        return clubesBye;
    }

    public void setClubesBye(List<CampeonatoClubeResponseDTO> clubesBye) {
        this.clubesBye = clubesBye;
    }
}
