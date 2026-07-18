package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartidaDetalheResponseDTO {

    private Long campeonatoPartidaId;
    private Long campeonatoId;
    private Integer ordem;
    private StatusPartida status;

    private Long clubeMandanteId;
    private String clubeMandanteNome;
    private String clubeMandanteSigla;
    private ClubRank clubeMandanteRank;
    private Integer clubeMandanteCompetidor;

    private Long clubeVisitanteId;
    private String clubeVisitanteNome;
    private String clubeVisitanteSigla;
    private ClubRank clubeVisitanteRank;
    private Integer clubeVisitanteCompetidor;

    private Integer golsMandante;
    private Integer golsVisitante;
    private Boolean disputouPenaltis;
    private Integer penaltisMandante;
    private Integer penaltisVisitante;

    private Long clubeVencedorId;
    private String clubeVencedorNome;

    private List<CampeonatoAtletaPartidaDTO> atletasMandante = new ArrayList<>();
    private List<CampeonatoAtletaPartidaDTO> atletasVisitante = new ArrayList<>();
    private List<PartidaEventoResponseDTO> eventos = new ArrayList<>();

    public Long getCampeonatoPartidaId() {
        return campeonatoPartidaId;
    }

    public void setCampeonatoPartidaId(Long campeonatoPartidaId) {
        this.campeonatoPartidaId = campeonatoPartidaId;
    }

    public Long getCampeonatoId() {
        return campeonatoId;
    }

    public void setCampeonatoId(Long campeonatoId) {
        this.campeonatoId = campeonatoId;
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

    public Integer getGolsMandante() {
        return golsMandante;
    }

    public void setGolsMandante(Integer golsMandante) {
        this.golsMandante = golsMandante;
    }

    public Integer getGolsVisitante() {
        return golsVisitante;
    }

    public void setGolsVisitante(Integer golsVisitante) {
        this.golsVisitante = golsVisitante;
    }

    public Boolean getDisputouPenaltis() {
        return disputouPenaltis;
    }

    public void setDisputouPenaltis(Boolean disputouPenaltis) {
        this.disputouPenaltis = disputouPenaltis;
    }

    public Integer getPenaltisMandante() {
        return penaltisMandante;
    }

    public void setPenaltisMandante(Integer penaltisMandante) {
        this.penaltisMandante = penaltisMandante;
    }

    public Integer getPenaltisVisitante() {
        return penaltisVisitante;
    }

    public void setPenaltisVisitante(Integer penaltisVisitante) {
        this.penaltisVisitante = penaltisVisitante;
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

    public List<CampeonatoAtletaPartidaDTO> getAtletasMandante() {
        return atletasMandante;
    }

    public void setAtletasMandante(List<CampeonatoAtletaPartidaDTO> atletasMandante) {
        this.atletasMandante = atletasMandante;
    }

    public List<CampeonatoAtletaPartidaDTO> getAtletasVisitante() {
        return atletasVisitante;
    }

    public void setAtletasVisitante(List<CampeonatoAtletaPartidaDTO> atletasVisitante) {
        this.atletasVisitante = atletasVisitante;
    }

    public List<PartidaEventoResponseDTO> getEventos() {
        return eventos;
    }

    public void setEventos(List<PartidaEventoResponseDTO> eventos) {
        this.eventos = eventos;
    }
}
