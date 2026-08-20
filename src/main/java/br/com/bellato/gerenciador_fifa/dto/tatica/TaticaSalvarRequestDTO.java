package br.com.bellato.gerenciador_fifa.dto.tatica;

import java.util.ArrayList;
import java.util.List;

import br.com.bellato.gerenciador_fifa.enums.FormacaoTatica;

public class TaticaSalvarRequestDTO {

    private FormacaoTatica formacao;
    private String anotacoes;
    private Long capitaoAtletaId;
    private Long batedorPenaltisAtletaId;
    private Long batedorFaltaAtletaId;
    private Long batedorEscanteioEsquerdoAtletaId;
    private Long batedorEscanteioDireitoAtletaId;
    private List<TaticaJogadorRequestDTO> jogadores = new ArrayList<>();

    public FormacaoTatica getFormacao() {
        return formacao;
    }

    public void setFormacao(FormacaoTatica formacao) {
        this.formacao = formacao;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public Long getCapitaoAtletaId() {
        return capitaoAtletaId;
    }

    public void setCapitaoAtletaId(Long capitaoAtletaId) {
        this.capitaoAtletaId = capitaoAtletaId;
    }

    public Long getBatedorPenaltisAtletaId() {
        return batedorPenaltisAtletaId;
    }

    public void setBatedorPenaltisAtletaId(Long batedorPenaltisAtletaId) {
        this.batedorPenaltisAtletaId = batedorPenaltisAtletaId;
    }

    public Long getBatedorFaltaAtletaId() {
        return batedorFaltaAtletaId;
    }

    public void setBatedorFaltaAtletaId(Long batedorFaltaAtletaId) {
        this.batedorFaltaAtletaId = batedorFaltaAtletaId;
    }

    public Long getBatedorEscanteioEsquerdoAtletaId() {
        return batedorEscanteioEsquerdoAtletaId;
    }

    public void setBatedorEscanteioEsquerdoAtletaId(Long batedorEscanteioEsquerdoAtletaId) {
        this.batedorEscanteioEsquerdoAtletaId = batedorEscanteioEsquerdoAtletaId;
    }

    public Long getBatedorEscanteioDireitoAtletaId() {
        return batedorEscanteioDireitoAtletaId;
    }

    public void setBatedorEscanteioDireitoAtletaId(Long batedorEscanteioDireitoAtletaId) {
        this.batedorEscanteioDireitoAtletaId = batedorEscanteioDireitoAtletaId;
    }

    public List<TaticaJogadorRequestDTO> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<TaticaJogadorRequestDTO> jogadores) {
        this.jogadores = jogadores != null ? jogadores : new ArrayList<>();
    }
}
