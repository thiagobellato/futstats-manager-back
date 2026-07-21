package br.com.bellato.gerenciador_fifa.dto.campeonato;

public class CampeonatoTransferirAtletaRequestDTO {

    /** Vínculo ativo no campeonato (transferência interna). */
    private Long campeonatoAtletaId;

    /** Atleta do banco global (importação / transferência a partir de qualquer clube). */
    private Long atletaOrigemId;

    private Long novoCampeonatoClubeId;

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

    public Long getNovoCampeonatoClubeId() {
        return novoCampeonatoClubeId;
    }

    public void setNovoCampeonatoClubeId(Long novoCampeonatoClubeId) {
        this.novoCampeonatoClubeId = novoCampeonatoClubeId;
    }
}
