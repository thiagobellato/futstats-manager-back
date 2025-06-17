package br.com.bellato.gerenciador_fifa.dto.estatistica_atleta;

public class EstatisticaAtletaResponseDTO {

    private Long estatisticaAtletaId;
    private String nomeAtleta;
    private Integer gols;
    private Integer assistencias;

    public EstatisticaAtletaResponseDTO() {
    }

    public EstatisticaAtletaResponseDTO(Long estatisticaAtletaId, String nomeAtleta, Integer gols,
            Integer assistencias) {
        this.estatisticaAtletaId = estatisticaAtletaId;
        this.nomeAtleta = nomeAtleta;
        this.gols = gols;
        this.assistencias = assistencias;
    }

    public Long getEstatisticaAtletaId() {
        return estatisticaAtletaId;
    }

    public void setEstatisticaAtletaId(Long estatisticaAtletaId) {
        this.estatisticaAtletaId = estatisticaAtletaId;
    }

    public String getNomeAtleta() {
        return nomeAtleta;
    }

    public void setNomeAtleta(String nomeAtleta) {
        this.nomeAtleta = nomeAtleta;
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

    @Override
    public String toString() {
        return "EstatisticaAtletaResponseDTO [estatisticaAtletaId=" + estatisticaAtletaId + ", nomeAtleta=" + nomeAtleta
                + ", gols=" + gols + ", assistencias=" + assistencias + "]";
    }

}
