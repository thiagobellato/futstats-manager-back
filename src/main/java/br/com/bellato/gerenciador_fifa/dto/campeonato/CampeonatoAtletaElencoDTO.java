package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoAtletaElencoDTO {

    private Long campeonatoAtletaId;
    private Long atletaOrigemId;
    private String identidade;
    private String nome;
    private String sobrenome;
    private String nacionalidade;
    private PosicaoFutebol posicao;
    private Long campeonatoClubeId;
    private String clubeNome;
    private Boolean ativo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer gols;
    private Integer assistencias;
    private Integer cartoesAmarelos;
    private Integer cartoesVermelhos;
    private List<CampeonatoAtletaHistoricoClubeDTO> historico = new ArrayList<>();

    public Long getCampeonatoAtletaId() {
        return campeonatoAtletaId;
    }

    public void setCampeonatoAtletaId(Long campeonatoAtletaId) {
        this.campeonatoAtletaId = campeonatoAtletaId;
    }

    public Long getAtletaOrigemId() {
        return atletaOrigemId;
    }

    public void setAtletaOrigemId(Long atletaOrigemId) {
        this.atletaOrigemId = atletaOrigemId;
    }

    public String getIdentidade() {
        return identidade;
    }

    public void setIdentidade(String identidade) {
        this.identidade = identidade;
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

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public PosicaoFutebol getPosicao() {
        return posicao;
    }

    public void setPosicao(PosicaoFutebol posicao) {
        this.posicao = posicao;
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
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

    public List<CampeonatoAtletaHistoricoClubeDTO> getHistorico() {
        return historico;
    }

    public void setHistorico(List<CampeonatoAtletaHistoricoClubeDTO> historico) {
        this.historico = historico;
    }
}
