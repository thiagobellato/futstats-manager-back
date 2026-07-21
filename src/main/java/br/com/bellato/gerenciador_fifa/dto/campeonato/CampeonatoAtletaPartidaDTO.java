package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoAtletaPartidaDTO {

    private Long campeonatoAtletaId;
    private Long campeonatoClubeId;
    private String nome;
    private String sobrenome;
    private PosicaoFutebol posicao;
    private Boolean suspenso;
    private String motivoSuspensao;

    public Long getCampeonatoAtletaId() {
        return campeonatoAtletaId;
    }

    public void setCampeonatoAtletaId(Long campeonatoAtletaId) {
        this.campeonatoAtletaId = campeonatoAtletaId;
    }

    public Long getCampeonatoClubeId() {
        return campeonatoClubeId;
    }

    public void setCampeonatoClubeId(Long campeonatoClubeId) {
        this.campeonatoClubeId = campeonatoClubeId;
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

    public PosicaoFutebol getPosicao() {
        return posicao;
    }

    public void setPosicao(PosicaoFutebol posicao) {
        this.posicao = posicao;
    }

    public Boolean getSuspenso() {
        return suspenso;
    }

    public void setSuspenso(Boolean suspenso) {
        this.suspenso = suspenso;
    }

    public String getMotivoSuspensao() {
        return motivoSuspensao;
    }

    public void setMotivoSuspensao(String motivoSuspensao) {
        this.motivoSuspensao = motivoSuspensao;
    }
}
