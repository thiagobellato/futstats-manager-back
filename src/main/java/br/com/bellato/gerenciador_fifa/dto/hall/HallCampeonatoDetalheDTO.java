package br.com.bellato.gerenciador_fifa.dto.hall;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoEstatisticasDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClassificacaoClubeDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RankingAtletaCampeonatoDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallCampeonatoDetalheDTO {

    private HallCampeonatoResumoDTO resumo;
    private CampeonatoResponseCompletoDTO campeonato;
    private CampeonatoEstatisticasDTO estatisticas;
    private List<RankingAtletaCampeonatoDTO> golsContra = new ArrayList<>();
    private List<RankingAtletaCampeonatoDTO> goleirosMenosVazados = new ArrayList<>();
    private ClassificacaoClubeDTO piorAtaque;
    private ClassificacaoClubeDTO piorDefesa;
    private ClassificacaoClubeDTO menorSaldo;
    private HallGoleadaDTO maiorGoleada;
    private HallSequenciaDTO maiorSequenciaVitorias;
    private HallSequenciaDTO maiorSequenciaSemPerder;

    public HallCampeonatoResumoDTO getResumo() {
        return resumo;
    }

    public void setResumo(HallCampeonatoResumoDTO resumo) {
        this.resumo = resumo;
    }

    public CampeonatoResponseCompletoDTO getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(CampeonatoResponseCompletoDTO campeonato) {
        this.campeonato = campeonato;
    }

    public CampeonatoEstatisticasDTO getEstatisticas() {
        return estatisticas;
    }

    public void setEstatisticas(CampeonatoEstatisticasDTO estatisticas) {
        this.estatisticas = estatisticas;
    }

    public List<RankingAtletaCampeonatoDTO> getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(List<RankingAtletaCampeonatoDTO> golsContra) {
        this.golsContra = golsContra;
    }

    public List<RankingAtletaCampeonatoDTO> getGoleirosMenosVazados() {
        return goleirosMenosVazados;
    }

    public void setGoleirosMenosVazados(List<RankingAtletaCampeonatoDTO> goleirosMenosVazados) {
        this.goleirosMenosVazados = goleirosMenosVazados;
    }

    public ClassificacaoClubeDTO getPiorAtaque() {
        return piorAtaque;
    }

    public void setPiorAtaque(ClassificacaoClubeDTO piorAtaque) {
        this.piorAtaque = piorAtaque;
    }

    public ClassificacaoClubeDTO getPiorDefesa() {
        return piorDefesa;
    }

    public void setPiorDefesa(ClassificacaoClubeDTO piorDefesa) {
        this.piorDefesa = piorDefesa;
    }

    public ClassificacaoClubeDTO getMenorSaldo() {
        return menorSaldo;
    }

    public void setMenorSaldo(ClassificacaoClubeDTO menorSaldo) {
        this.menorSaldo = menorSaldo;
    }

    public HallGoleadaDTO getMaiorGoleada() {
        return maiorGoleada;
    }

    public void setMaiorGoleada(HallGoleadaDTO maiorGoleada) {
        this.maiorGoleada = maiorGoleada;
    }

    public HallSequenciaDTO getMaiorSequenciaVitorias() {
        return maiorSequenciaVitorias;
    }

    public void setMaiorSequenciaVitorias(HallSequenciaDTO maiorSequenciaVitorias) {
        this.maiorSequenciaVitorias = maiorSequenciaVitorias;
    }

    public HallSequenciaDTO getMaiorSequenciaSemPerder() {
        return maiorSequenciaSemPerder;
    }

    public void setMaiorSequenciaSemPerder(HallSequenciaDTO maiorSequenciaSemPerder) {
        this.maiorSequenciaSemPerder = maiorSequenciaSemPerder;
    }
}
