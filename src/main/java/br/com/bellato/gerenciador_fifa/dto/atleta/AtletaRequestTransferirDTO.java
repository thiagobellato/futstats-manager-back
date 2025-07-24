package br.com.bellato.gerenciador_fifa.dto.atleta;

public class AtletaRequestTransferirDTO {

    private Long atletaId;
    private Long novoClubeId;

    public Long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
    }

    public Long getNovoClubeId() {
        return novoClubeId;
    }

    public void setNovoClubeId(Long novoClubeId) {
        this.novoClubeId = novoClubeId;
    }

}
