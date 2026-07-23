package br.com.bellato.gerenciador_fifa.dto.hall;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallGoleadaDTO {

    private Long partidaId;
    private String mandanteNome;
    private String visitanteNome;
    private Integer golsMandante;
    private Integer golsVisitante;
    private Integer diferenca;
    private String placar;
    private String rodadaNome;

    public Long getPartidaId() {
        return partidaId;
    }

    public void setPartidaId(Long partidaId) {
        this.partidaId = partidaId;
    }

    public String getMandanteNome() {
        return mandanteNome;
    }

    public void setMandanteNome(String mandanteNome) {
        this.mandanteNome = mandanteNome;
    }

    public String getVisitanteNome() {
        return visitanteNome;
    }

    public void setVisitanteNome(String visitanteNome) {
        this.visitanteNome = visitanteNome;
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

    public Integer getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(Integer diferenca) {
        this.diferenca = diferenca;
    }

    public String getPlacar() {
        return placar;
    }

    public void setPlacar(String placar) {
        this.placar = placar;
    }

    public String getRodadaNome() {
        return rodadaNome;
    }

    public void setRodadaNome(String rodadaNome) {
        this.rodadaNome = rodadaNome;
    }
}
