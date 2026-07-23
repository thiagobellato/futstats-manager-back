package br.com.bellato.gerenciador_fifa.dto.hall;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallJogadorPerfilDTO {

    private Long atletaId;
    private String nome;
    private String sobrenome;
    private String posicao;
    private String clubeAtualNome;
    private Integer gols;
    private Integer assistencias;
    private Integer golsContra;
    private Integer cartoesAmarelos;
    private Integer cartoesVermelhos;
    private Integer transferencias;
    private Integer titulos;
    private Integer campeonatosDisputados;
    private List<String> clubesDefendidos = new ArrayList<>();
    private List<HallTimelineJogadorItemDTO> timeline = new ArrayList<>();

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

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public String getClubeAtualNome() {
        return clubeAtualNome;
    }

    public void setClubeAtualNome(String clubeAtualNome) {
        this.clubeAtualNome = clubeAtualNome;
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

    public Integer getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(Integer golsContra) {
        this.golsContra = golsContra;
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

    public Integer getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(Integer transferencias) {
        this.transferencias = transferencias;
    }

    public Integer getTitulos() {
        return titulos;
    }

    public void setTitulos(Integer titulos) {
        this.titulos = titulos;
    }

    public Integer getCampeonatosDisputados() {
        return campeonatosDisputados;
    }

    public void setCampeonatosDisputados(Integer campeonatosDisputados) {
        this.campeonatosDisputados = campeonatosDisputados;
    }

    public List<String> getClubesDefendidos() {
        return clubesDefendidos;
    }

    public void setClubesDefendidos(List<String> clubesDefendidos) {
        this.clubesDefendidos = clubesDefendidos;
    }

    public List<HallTimelineJogadorItemDTO> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<HallTimelineJogadorItemDTO> timeline) {
        this.timeline = timeline;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HallTimelineJogadorItemDTO {
        private Long campeonatoId;
        private String campeonatoNome;
        private Integer ano;
        private LocalDateTime dataFinalizacao;
        private Boolean tituloConquistado;
        private Integer gols;
        private Integer assistencias;
        private Integer golsContra;
        private Integer cartoesAmarelos;
        private Integer cartoesVermelhos;
        private Integer transferencias;
        private String clubesDefendidos;

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

        public Boolean getTituloConquistado() {
            return tituloConquistado;
        }

        public void setTituloConquistado(Boolean tituloConquistado) {
            this.tituloConquistado = tituloConquistado;
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

        public Integer getGolsContra() {
            return golsContra;
        }

        public void setGolsContra(Integer golsContra) {
            this.golsContra = golsContra;
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

        public Integer getTransferencias() {
            return transferencias;
        }

        public void setTransferencias(Integer transferencias) {
            this.transferencias = transferencias;
        }

        public String getClubesDefendidos() {
            return clubesDefendidos;
        }

        public void setClubesDefendidos(String clubesDefendidos) {
            this.clubesDefendidos = clubesDefendidos;
        }
    }
}
