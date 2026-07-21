package br.com.bellato.gerenciador_fifa.service.transferencia;

/**
 * Comando de transferência de atleta.
 * A estratégia escolhida decide onde persistir (global vs snapshot do campeonato).
 */
public class AthleteTransferCommand {

    public enum Escopo {
        GLOBAL,
        CAMPEONATO
    }

    private Escopo escopo;
    private Long campeonatoId;

    /** ID do atleta global (escopo GLOBAL ou importação para o campeonato). */
    private Long atletaGlobalId;

    /** ID do vínculo ativo no campeonato (transferência interna). */
    private Long campeonatoAtletaId;

    /** Clube global de destino (escopo GLOBAL). */
    private Long novoClubeGlobalId;

    /** Clube snapshot de destino (escopo CAMPEONATO). */
    private Long novoCampeonatoClubeId;

    public Escopo getEscopo() {
        return escopo;
    }

    public void setEscopo(Escopo escopo) {
        this.escopo = escopo;
    }

    public Long getCampeonatoId() {
        return campeonatoId;
    }

    public void setCampeonatoId(Long campeonatoId) {
        this.campeonatoId = campeonatoId;
    }

    public Long getAtletaGlobalId() {
        return atletaGlobalId;
    }

    public void setAtletaGlobalId(Long atletaGlobalId) {
        this.atletaGlobalId = atletaGlobalId;
    }

    public Long getCampeonatoAtletaId() {
        return campeonatoAtletaId;
    }

    public void setCampeonatoAtletaId(Long campeonatoAtletaId) {
        this.campeonatoAtletaId = campeonatoAtletaId;
    }

    public Long getNovoClubeGlobalId() {
        return novoClubeGlobalId;
    }

    public void setNovoClubeGlobalId(Long novoClubeGlobalId) {
        this.novoClubeGlobalId = novoClubeGlobalId;
    }

    public Long getNovoCampeonatoClubeId() {
        return novoCampeonatoClubeId;
    }

    public void setNovoCampeonatoClubeId(Long novoCampeonatoClubeId) {
        this.novoCampeonatoClubeId = novoCampeonatoClubeId;
    }
}
