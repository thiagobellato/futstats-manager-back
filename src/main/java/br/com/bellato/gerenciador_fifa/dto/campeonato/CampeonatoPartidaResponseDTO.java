package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoPartidaResponseDTO {

    private Long campeonatoPartidaId;
    private Integer ordem;
    private StatusPartida status;
    private Long clubeMandanteId;
    private String clubeMandanteNome;
    private String clubeMandanteSigla;
    private ClubRank clubeMandanteRank;
    private Integer clubeMandanteCompetidor;
    private Boolean clubeMandanteCampeaoAnterior;
    private Long clubeVisitanteId;
    private String clubeVisitanteNome;
    private String clubeVisitanteSigla;
    private ClubRank clubeVisitanteRank;
    private Integer clubeVisitanteCompetidor;
    private Boolean clubeVisitanteCampeaoAnterior;
    private Long clubeVencedorId;
    private String clubeVencedorNome;

    public Long getCampeonatoPartidaId() {
        return campeonatoPartidaId;
    }

    public void setCampeonatoPartidaId(Long campeonatoPartidaId) {
        this.campeonatoPartidaId = campeonatoPartidaId;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public StatusPartida getStatus() {
        return status;
    }

    public void setStatus(StatusPartida status) {
        this.status = status;
    }

    public Long getClubeMandanteId() {
        return clubeMandanteId;
    }

    public void setClubeMandanteId(Long clubeMandanteId) {
        this.clubeMandanteId = clubeMandanteId;
    }

    public String getClubeMandanteNome() {
        return clubeMandanteNome;
    }

    public void setClubeMandanteNome(String clubeMandanteNome) {
        this.clubeMandanteNome = clubeMandanteNome;
    }

    public String getClubeMandanteSigla() {
        return clubeMandanteSigla;
    }

    public void setClubeMandanteSigla(String clubeMandanteSigla) {
        this.clubeMandanteSigla = clubeMandanteSigla;
    }

    public ClubRank getClubeMandanteRank() {
        return clubeMandanteRank;
    }

    public void setClubeMandanteRank(ClubRank clubeMandanteRank) {
        this.clubeMandanteRank = clubeMandanteRank;
    }

    public Integer getClubeMandanteCompetidor() {
        return clubeMandanteCompetidor;
    }

    public void setClubeMandanteCompetidor(Integer clubeMandanteCompetidor) {
        this.clubeMandanteCompetidor = clubeMandanteCompetidor;
    }

    public Boolean getClubeMandanteCampeaoAnterior() {
        return clubeMandanteCampeaoAnterior;
    }

    public void setClubeMandanteCampeaoAnterior(Boolean clubeMandanteCampeaoAnterior) {
        this.clubeMandanteCampeaoAnterior = clubeMandanteCampeaoAnterior;
    }

    public Long getClubeVisitanteId() {
        return clubeVisitanteId;
    }

    public void setClubeVisitanteId(Long clubeVisitanteId) {
        this.clubeVisitanteId = clubeVisitanteId;
    }

    public String getClubeVisitanteNome() {
        return clubeVisitanteNome;
    }

    public void setClubeVisitanteNome(String clubeVisitanteNome) {
        this.clubeVisitanteNome = clubeVisitanteNome;
    }

    public String getClubeVisitanteSigla() {
        return clubeVisitanteSigla;
    }

    public void setClubeVisitanteSigla(String clubeVisitanteSigla) {
        this.clubeVisitanteSigla = clubeVisitanteSigla;
    }

    public ClubRank getClubeVisitanteRank() {
        return clubeVisitanteRank;
    }

    public void setClubeVisitanteRank(ClubRank clubeVisitanteRank) {
        this.clubeVisitanteRank = clubeVisitanteRank;
    }

    public Integer getClubeVisitanteCompetidor() {
        return clubeVisitanteCompetidor;
    }

    public void setClubeVisitanteCompetidor(Integer clubeVisitanteCompetidor) {
        this.clubeVisitanteCompetidor = clubeVisitanteCompetidor;
    }

    public Boolean getClubeVisitanteCampeaoAnterior() {
        return clubeVisitanteCampeaoAnterior;
    }

    public void setClubeVisitanteCampeaoAnterior(Boolean clubeVisitanteCampeaoAnterior) {
        this.clubeVisitanteCampeaoAnterior = clubeVisitanteCampeaoAnterior;
    }

    public Long getClubeVencedorId() {
        return clubeVencedorId;
    }

    public void setClubeVencedorId(Long clubeVencedorId) {
        this.clubeVencedorId = clubeVencedorId;
    }

    public String getClubeVencedorNome() {
        return clubeVencedorNome;
    }

    public void setClubeVencedorNome(String clubeVencedorNome) {
        this.clubeVencedorNome = clubeVencedorNome;
    }
}
