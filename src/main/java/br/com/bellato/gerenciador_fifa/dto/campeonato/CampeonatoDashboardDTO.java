package br.com.bellato.gerenciador_fifa.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoDashboardDTO {

    private String campeaoNome;
    private Long campeaoClubeId;
    private String viceNome;
    private Long viceClubeId;

    private RankingAtletaCampeonatoDTO artilheiro;
    private RankingAtletaCampeonatoDTO liderAssistencias;
    private RankingAtletaCampeonatoDTO maisCartoesAmarelos;
    private RankingAtletaCampeonatoDTO maisCartoesVermelhos;

    private ClassificacaoClubeDTO melhorAtaque;
    private ClassificacaoClubeDTO melhorDefesa;
    private ClassificacaoClubeDTO maiorSaldo;

    private Integer quantidadePartidas;
    private Integer quantidadeGols;
    private Double mediaGols;
    private Integer quantidadeCartoes;
    private Integer quantidadeTransferencias;
    private Integer quantidadeAtletasCriados;

    public String getCampeaoNome() {
        return campeaoNome;
    }

    public void setCampeaoNome(String campeaoNome) {
        this.campeaoNome = campeaoNome;
    }

    public Long getCampeaoClubeId() {
        return campeaoClubeId;
    }

    public void setCampeaoClubeId(Long campeaoClubeId) {
        this.campeaoClubeId = campeaoClubeId;
    }

    public String getViceNome() {
        return viceNome;
    }

    public void setViceNome(String viceNome) {
        this.viceNome = viceNome;
    }

    public Long getViceClubeId() {
        return viceClubeId;
    }

    public void setViceClubeId(Long viceClubeId) {
        this.viceClubeId = viceClubeId;
    }

    public RankingAtletaCampeonatoDTO getArtilheiro() {
        return artilheiro;
    }

    public void setArtilheiro(RankingAtletaCampeonatoDTO artilheiro) {
        this.artilheiro = artilheiro;
    }

    public RankingAtletaCampeonatoDTO getLiderAssistencias() {
        return liderAssistencias;
    }

    public void setLiderAssistencias(RankingAtletaCampeonatoDTO liderAssistencias) {
        this.liderAssistencias = liderAssistencias;
    }

    public RankingAtletaCampeonatoDTO getMaisCartoesAmarelos() {
        return maisCartoesAmarelos;
    }

    public void setMaisCartoesAmarelos(RankingAtletaCampeonatoDTO maisCartoesAmarelos) {
        this.maisCartoesAmarelos = maisCartoesAmarelos;
    }

    public RankingAtletaCampeonatoDTO getMaisCartoesVermelhos() {
        return maisCartoesVermelhos;
    }

    public void setMaisCartoesVermelhos(RankingAtletaCampeonatoDTO maisCartoesVermelhos) {
        this.maisCartoesVermelhos = maisCartoesVermelhos;
    }

    public ClassificacaoClubeDTO getMelhorAtaque() {
        return melhorAtaque;
    }

    public void setMelhorAtaque(ClassificacaoClubeDTO melhorAtaque) {
        this.melhorAtaque = melhorAtaque;
    }

    public ClassificacaoClubeDTO getMelhorDefesa() {
        return melhorDefesa;
    }

    public void setMelhorDefesa(ClassificacaoClubeDTO melhorDefesa) {
        this.melhorDefesa = melhorDefesa;
    }

    public ClassificacaoClubeDTO getMaiorSaldo() {
        return maiorSaldo;
    }

    public void setMaiorSaldo(ClassificacaoClubeDTO maiorSaldo) {
        this.maiorSaldo = maiorSaldo;
    }

    public Integer getQuantidadePartidas() {
        return quantidadePartidas;
    }

    public void setQuantidadePartidas(Integer quantidadePartidas) {
        this.quantidadePartidas = quantidadePartidas;
    }

    public Integer getQuantidadeGols() {
        return quantidadeGols;
    }

    public void setQuantidadeGols(Integer quantidadeGols) {
        this.quantidadeGols = quantidadeGols;
    }

    public Double getMediaGols() {
        return mediaGols;
    }

    public void setMediaGols(Double mediaGols) {
        this.mediaGols = mediaGols;
    }

    public Integer getQuantidadeCartoes() {
        return quantidadeCartoes;
    }

    public void setQuantidadeCartoes(Integer quantidadeCartoes) {
        this.quantidadeCartoes = quantidadeCartoes;
    }

    public Integer getQuantidadeTransferencias() {
        return quantidadeTransferencias;
    }

    public void setQuantidadeTransferencias(Integer quantidadeTransferencias) {
        this.quantidadeTransferencias = quantidadeTransferencias;
    }

    public Integer getQuantidadeAtletasCriados() {
        return quantidadeAtletasCriados;
    }

    public void setQuantidadeAtletasCriados(Integer quantidadeAtletasCriados) {
        this.quantidadeAtletasCriados = quantidadeAtletasCriados;
    }
}
