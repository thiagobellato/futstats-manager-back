package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartidaEventoResponseDTO {

    private Long campeonatoPartidaEventoId;
    private TipoEventoPartida tipo;
    private Long campeonatoAtletaId;
    private String atletaNome;
    private String atletaSobrenome;
    private Long campeonatoClubeId;
    private String clubeNome;
    private Integer ordem;

    public Long getCampeonatoPartidaEventoId() {
        return campeonatoPartidaEventoId;
    }

    public void setCampeonatoPartidaEventoId(Long campeonatoPartidaEventoId) {
        this.campeonatoPartidaEventoId = campeonatoPartidaEventoId;
    }

    public TipoEventoPartida getTipo() {
        return tipo;
    }

    public void setTipo(TipoEventoPartida tipo) {
        this.tipo = tipo;
    }

    public Long getCampeonatoAtletaId() {
        return campeonatoAtletaId;
    }

    public void setCampeonatoAtletaId(Long campeonatoAtletaId) {
        this.campeonatoAtletaId = campeonatoAtletaId;
    }

    public String getAtletaNome() {
        return atletaNome;
    }

    public void setAtletaNome(String atletaNome) {
        this.atletaNome = atletaNome;
    }

    public String getAtletaSobrenome() {
        return atletaSobrenome;
    }

    public void setAtletaSobrenome(String atletaSobrenome) {
        this.atletaSobrenome = atletaSobrenome;
    }

    public Long getCampeonatoClubeId() {
        return campeonatoClubeId;
    }

    public void setCampeonatoClubeId(Long campeonatoClubeId) {
        this.campeonatoClubeId = campeonatoClubeId;
    }

    public String getClubeNome() {
        return clubeNome;
    }

    public void setClubeNome(String clubeNome) {
        this.clubeNome = clubeNome;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
