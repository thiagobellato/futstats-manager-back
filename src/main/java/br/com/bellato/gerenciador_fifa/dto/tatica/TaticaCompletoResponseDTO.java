package br.com.bellato.gerenciador_fifa.dto.tatica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.enums.FormacaoTatica;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaticaCompletoResponseDTO {

    private Long taticaUsuarioClubeId;
    private Long clubeId;
    private String clubeNome;
    private String clubeSigla;
    private FormacaoTatica formacao;
    private String anotacoes;
    private Long capitaoAtletaId;
    private Long batedorPenaltisAtletaId;
    private Long batedorFaltaAtletaId;
    private Long batedorEscanteioEsquerdoAtletaId;
    private Long batedorEscanteioDireitoAtletaId;
    private LocalDateTime dataUltimaAtualizacao;
    private List<TaticaJogadorResponseDTO> titulares = new ArrayList<>();
    private List<TaticaJogadorResponseDTO> reservas = new ArrayList<>();
    private List<AtletaResponseCompletoDTO> elenco = new ArrayList<>();

    public Long getTaticaUsuarioClubeId() {
        return taticaUsuarioClubeId;
    }

    public void setTaticaUsuarioClubeId(Long taticaUsuarioClubeId) {
        this.taticaUsuarioClubeId = taticaUsuarioClubeId;
    }

    public Long getClubeId() {
        return clubeId;
    }

    public void setClubeId(Long clubeId) {
        this.clubeId = clubeId;
    }

    public String getClubeNome() {
        return clubeNome;
    }

    public void setClubeNome(String clubeNome) {
        this.clubeNome = clubeNome;
    }

    public String getClubeSigla() {
        return clubeSigla;
    }

    public void setClubeSigla(String clubeSigla) {
        this.clubeSigla = clubeSigla;
    }

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

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<TaticaJogadorResponseDTO> getTitulares() {
        return titulares;
    }

    public void setTitulares(List<TaticaJogadorResponseDTO> titulares) {
        this.titulares = titulares != null ? titulares : new ArrayList<>();
    }

    public List<TaticaJogadorResponseDTO> getReservas() {
        return reservas;
    }

    public void setReservas(List<TaticaJogadorResponseDTO> reservas) {
        this.reservas = reservas != null ? reservas : new ArrayList<>();
    }

    public List<AtletaResponseCompletoDTO> getElenco() {
        return elenco;
    }

    public void setElenco(List<AtletaResponseCompletoDTO> elenco) {
        this.elenco = elenco != null ? elenco : new ArrayList<>();
    }
}
