package br.com.bellato.gerenciador_fifa.dto.hall;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallCompetidorPerfilDTO {

    private String nome;
    private Integer titulos;
    private Integer vices;
    private Integer finais;
    private Integer participacoes;
    private Integer jogos;
    private Integer vitorias;
    private Integer empates;
    private Integer derrotas;
    private Double aproveitamento;
    private List<String> clubesUtilizados = new ArrayList<>();
    private List<HallTimelineCompetidorItemDTO> timeline = new ArrayList<>();

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getTitulos() {
        return titulos;
    }

    public void setTitulos(Integer titulos) {
        this.titulos = titulos;
    }

    public Integer getVices() {
        return vices;
    }

    public void setVices(Integer vices) {
        this.vices = vices;
    }

    public Integer getFinais() {
        return finais;
    }

    public void setFinais(Integer finais) {
        this.finais = finais;
    }

    public Integer getParticipacoes() {
        return participacoes;
    }

    public void setParticipacoes(Integer participacoes) {
        this.participacoes = participacoes;
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

    public Double getAproveitamento() {
        return aproveitamento;
    }

    public void setAproveitamento(Double aproveitamento) {
        this.aproveitamento = aproveitamento;
    }

    public List<String> getClubesUtilizados() {
        return clubesUtilizados;
    }

    public void setClubesUtilizados(List<String> clubesUtilizados) {
        this.clubesUtilizados = clubesUtilizados;
    }

    public List<HallTimelineCompetidorItemDTO> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<HallTimelineCompetidorItemDTO> timeline) {
        this.timeline = timeline;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HallTimelineCompetidorItemDTO {
        private Long campeonatoId;
        private String campeonatoNome;
        private Integer ano;
        private LocalDateTime dataFinalizacao;
        private Boolean campeao;
        private Boolean vice;
        private List<String> clubes = new ArrayList<>();
        private Integer jogos;
        private Integer vitorias;
        private Integer empates;
        private Integer derrotas;

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

        public Boolean getCampeao() {
            return campeao;
        }

        public void setCampeao(Boolean campeao) {
            this.campeao = campeao;
        }

        public Boolean getVice() {
            return vice;
        }

        public void setVice(Boolean vice) {
            this.vice = vice;
        }

        public List<String> getClubes() {
            return clubes;
        }

        public void setClubes(List<String> clubes) {
            this.clubes = clubes;
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
    }
}
