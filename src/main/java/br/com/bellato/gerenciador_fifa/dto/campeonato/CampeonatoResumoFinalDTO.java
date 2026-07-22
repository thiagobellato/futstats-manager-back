package br.com.bellato.gerenciador_fifa.dto.campeonato;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Pré-visualização da finalização — não persiste alterações.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampeonatoResumoFinalDTO {

    private Long campeonatoId;
    private String nome;
    private Integer formato;

    private String campeaoClubeNome;
    private Long campeaoClubeId;
    private Integer campeaoCompetidor;
    private String campeaoCompetidorNome;

    private String viceCampeaoClubeNome;
    private Long viceCampeaoClubeId;
    private Integer viceCompetidor;
    private String viceCompetidorNome;

    private RankingAtletaCampeonatoDTO artilheiro;
    private RankingAtletaCampeonatoDTO liderAssistencias;
    private RankingAtletaCampeonatoDTO maisCartoesAmarelos;
    private RankingAtletaCampeonatoDTO maisCartoesVermelhos;

    private ClassificacaoClubeDTO melhorAtaque;
    private ClassificacaoClubeDTO melhorDefesa;
    private ClassificacaoClubeDTO maiorSaldo;

    private List<ClassificacaoClubeDTO> classificacao = new ArrayList<>();

    public Long getCampeonatoId() {
        return campeonatoId;
    }

    public void setCampeonatoId(Long campeonatoId) {
        this.campeonatoId = campeonatoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getFormato() {
        return formato;
    }

    public void setFormato(Integer formato) {
        this.formato = formato;
    }

    public String getCampeaoClubeNome() {
        return campeaoClubeNome;
    }

    public void setCampeaoClubeNome(String campeaoClubeNome) {
        this.campeaoClubeNome = campeaoClubeNome;
    }

    public Long getCampeaoClubeId() {
        return campeaoClubeId;
    }

    public void setCampeaoClubeId(Long campeaoClubeId) {
        this.campeaoClubeId = campeaoClubeId;
    }

    public Integer getCampeaoCompetidor() {
        return campeaoCompetidor;
    }

    public void setCampeaoCompetidor(Integer campeaoCompetidor) {
        this.campeaoCompetidor = campeaoCompetidor;
    }

    public String getCampeaoCompetidorNome() {
        return campeaoCompetidorNome;
    }

    public void setCampeaoCompetidorNome(String campeaoCompetidorNome) {
        this.campeaoCompetidorNome = campeaoCompetidorNome;
    }

    public String getViceCampeaoClubeNome() {
        return viceCampeaoClubeNome;
    }

    public void setViceCampeaoClubeNome(String viceCampeaoClubeNome) {
        this.viceCampeaoClubeNome = viceCampeaoClubeNome;
    }

    public Long getViceCampeaoClubeId() {
        return viceCampeaoClubeId;
    }

    public void setViceCampeaoClubeId(Long viceCampeaoClubeId) {
        this.viceCampeaoClubeId = viceCampeaoClubeId;
    }

    public Integer getViceCompetidor() {
        return viceCompetidor;
    }

    public void setViceCompetidor(Integer viceCompetidor) {
        this.viceCompetidor = viceCompetidor;
    }

    public String getViceCompetidorNome() {
        return viceCompetidorNome;
    }

    public void setViceCompetidorNome(String viceCompetidorNome) {
        this.viceCompetidorNome = viceCompetidorNome;
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

    public List<ClassificacaoClubeDTO> getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(List<ClassificacaoClubeDTO> classificacao) {
        this.classificacao = classificacao;
    }
}
