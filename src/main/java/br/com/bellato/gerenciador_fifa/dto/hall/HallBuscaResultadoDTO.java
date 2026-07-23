package br.com.bellato.gerenciador_fifa.dto.hall;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallBuscaResultadoDTO {

    private List<HallBuscaItemDTO> campeonatos = new ArrayList<>();
    private List<HallBuscaItemDTO> clubes = new ArrayList<>();
    private List<HallBuscaItemDTO> competidores = new ArrayList<>();
    private List<HallBuscaItemDTO> jogadores = new ArrayList<>();

    public List<HallBuscaItemDTO> getCampeonatos() {
        return campeonatos;
    }

    public void setCampeonatos(List<HallBuscaItemDTO> campeonatos) {
        this.campeonatos = campeonatos;
    }

    public List<HallBuscaItemDTO> getClubes() {
        return clubes;
    }

    public void setClubes(List<HallBuscaItemDTO> clubes) {
        this.clubes = clubes;
    }

    public List<HallBuscaItemDTO> getCompetidores() {
        return competidores;
    }

    public void setCompetidores(List<HallBuscaItemDTO> competidores) {
        this.competidores = competidores;
    }

    public List<HallBuscaItemDTO> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<HallBuscaItemDTO> jogadores) {
        this.jogadores = jogadores;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HallBuscaItemDTO {
        private String tipo;
        private Long id;
        private String nome;
        private String detalhe;
        private String path;

        public HallBuscaItemDTO() {
        }

        public HallBuscaItemDTO(String tipo, Long id, String nome, String detalhe, String path) {
            this.tipo = tipo;
            this.id = id;
            this.nome = nome;
            this.detalhe = detalhe;
            this.path = path;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getDetalhe() {
            return detalhe;
        }

        public void setDetalhe(String detalhe) {
            this.detalhe = detalhe;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
