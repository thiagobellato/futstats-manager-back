package br.com.bellato.gerenciador_fifa.mapper.campeonato;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoClubeResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoPartidaResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoRodadaResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClubeDisponivelDTO;
import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoDistribuicaoRank;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;
import br.com.bellato.gerenciador_fifa.model.Clube;

public class CampeonatoMapper {

    private CampeonatoMapper() {
    }

    public static CampeonatoResponseDTO toDTO(Campeonato campeonato) {
        CampeonatoResponseDTO dto = new CampeonatoResponseDTO();
        dto.setCampeonatoId(campeonato.getCampeonatoId());
        dto.setNome(campeonato.getNome());
        dto.setStatus(campeonato.getStatus() != null ? campeonato.getStatus().getCodigo() : null);
        return dto;
    }

    public static CampeonatoResponseCompletoDTO toDTOCompleto(Campeonato campeonato, long quantidadeAtletas,
            Map<Long, Long> atletasPorClube) {
        CampeonatoResponseCompletoDTO dto = new CampeonatoResponseCompletoDTO();
        dto.setCampeonatoId(campeonato.getCampeonatoId());
        dto.setNome(campeonato.getNome());
        dto.setQuantidadeClubes(campeonato.getQuantidadeClubes());
        dto.setStatus(campeonato.getStatus());
        dto.setCompetidor1Nome(campeonato.getCompetidor1Nome());
        dto.setCompetidor2Nome(campeonato.getCompetidor2Nome());
        dto.setPossuiCampeaoAnterior(campeonato.getPossuiCampeaoAnterior());
        dto.setCampeaoAnteriorCompetidor(campeonato.getCampeaoAnteriorCompetidor());
        dto.setCampeaoAnteriorClubeOrigemId(campeonato.getCampeaoAnteriorClubeOrigemId());
        dto.setDataCriacao(campeonato.getDataCriacao());
        dto.setQuantidadeAtletas(quantidadeAtletas);
        dto.setRodadaAtual(campeonato.getRodadaAtual());
        dto.setCampeaoCompetidor(campeonato.getCampeaoCompetidor());
        dto.setRemanejamentoNecessario(campeonato.getStatus() == StatusCampeonato.AGUARDANDO_REMANEJAMENTO);
        dto.setEscolhaCampeaoNecessaria(campeonato.getStatus() == StatusCampeonato.AGUARDANDO_ESCOLHA_DO_CAMPEAO);

        if (campeonato.getCampeaoClube() != null) {
            dto.setCampeaoClubeId(campeonato.getCampeaoClube().getCampeonatoClubeId());
            dto.setCampeaoClubeNome(campeonato.getCampeaoClube().getNome());
        }

        if (campeonato.getClubes() != null) {
            String campeaoAnteriorNome = campeonato.getClubes().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getCampeaoAnterior()))
                    .map(CampeonatoClube::getNome)
                    .findFirst()
                    .orElse(null);
            dto.setCampeaoAnteriorClubeNome(campeaoAnteriorNome);

            dto.setClubes(campeonato.getClubes().stream()
                    .map(clube -> toClubeDTO(clube, atletasPorClube))
                    .collect(Collectors.toList()));

            int classificados1 = (int) campeonato.getClubes().stream()
                    .filter(c -> c.isClassificado() && Objects.equals(c.getCompetidorNumero(), 1))
                    .count();
            int classificados2 = (int) campeonato.getClubes().stream()
                    .filter(c -> c.isClassificado() && Objects.equals(c.getCompetidorNumero(), 2))
                    .count();
            dto.setClubesClassificadosCompetidor1(classificados1);
            dto.setClubesClassificadosCompetidor2(classificados2);
        }

        if (campeonato.getDistribuicaoRanks() != null) {
            Map<ClubRank, Integer> distribuicao = new EnumMap<>(ClubRank.class);
            for (CampeonatoDistribuicaoRank item : campeonato.getDistribuicaoRanks()) {
                distribuicao.put(item.getRank(), item.getQuantidade());
            }
            dto.setDistribuicaoRanks(distribuicao);
        }

        if (campeonato.getRodadas() != null) {
            dto.setRodadas(campeonato.getRodadas().stream()
                    .sorted(Comparator.comparing(r -> r.getNumeroRodada() == null ? 0 : r.getNumeroRodada()))
                    .map(CampeonatoMapper::toRodadaDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static CampeonatoRodadaResponseDTO toRodadaDTO(CampeonatoRodada rodada) {
        CampeonatoRodadaResponseDTO dto = new CampeonatoRodadaResponseDTO();
        dto.setCampeonatoRodadaId(rodada.getCampeonatoRodadaId());
        dto.setNumeroRodada(rodada.getNumeroRodada());
        dto.setNome(rodada.getNome());
        dto.setStatus(rodada.getStatus());
        dto.setFaseAssincrona(rodada.getFaseAssincrona());
        dto.setCompetidorRemanejamento(rodada.getCompetidorRemanejamento());

        if (rodada.getPartidas() != null) {
            List<CampeonatoPartidaResponseDTO> partidas = rodada.getPartidas().stream()
                    .sorted(Comparator.comparing(p -> p.getOrdem() == null ? 0 : p.getOrdem()))
                    .map(CampeonatoMapper::toPartidaDTO)
                    .collect(Collectors.toList());
            dto.setPartidas(partidas);
            dto.setQuantidadePartidas(partidas.size());
            dto.setPartidasFinalizadas((int) partidas.stream()
                    .filter(p -> p.getStatus() == StatusPartida.FINALIZADA)
                    .count());
        }

        return dto;
    }

    public static CampeonatoPartidaResponseDTO toPartidaDTO(CampeonatoPartida partida) {
        CampeonatoPartidaResponseDTO dto = new CampeonatoPartidaResponseDTO();
        dto.setCampeonatoPartidaId(partida.getCampeonatoPartidaId());
        dto.setOrdem(partida.getOrdem());
        dto.setStatus(partida.getStatus());

        if (partida.getClubeMandante() != null) {
            dto.setClubeMandanteId(partida.getClubeMandante().getCampeonatoClubeId());
            dto.setClubeMandanteNome(partida.getClubeMandante().getNome());
            dto.setClubeMandanteSigla(partida.getClubeMandante().getSigla());
            dto.setClubeMandanteRank(partida.getClubeMandante().getRank());
            dto.setClubeMandanteCompetidor(partida.getClubeMandante().getCompetidorNumero());
            dto.setClubeMandanteCampeaoAnterior(partida.getClubeMandante().getCampeaoAnterior());
        }

        if (partida.getClubeVisitante() != null) {
            dto.setClubeVisitanteId(partida.getClubeVisitante().getCampeonatoClubeId());
            dto.setClubeVisitanteNome(partida.getClubeVisitante().getNome());
            dto.setClubeVisitanteSigla(partida.getClubeVisitante().getSigla());
            dto.setClubeVisitanteRank(partida.getClubeVisitante().getRank());
            dto.setClubeVisitanteCompetidor(partida.getClubeVisitante().getCompetidorNumero());
            dto.setClubeVisitanteCampeaoAnterior(partida.getClubeVisitante().getCampeaoAnterior());
        }

        if (partida.getClubeVencedor() != null) {
            dto.setClubeVencedorId(partida.getClubeVencedor().getCampeonatoClubeId());
            dto.setClubeVencedorNome(partida.getClubeVencedor().getNome());
        }

        dto.setGolsMandante(partida.getGolsMandante());
        dto.setGolsVisitante(partida.getGolsVisitante());
        dto.setDisputouPenaltis(partida.getDisputouPenaltis());
        dto.setPenaltisMandante(partida.getPenaltisMandante());
        dto.setPenaltisVisitante(partida.getPenaltisVisitante());

        return dto;
    }

    public static CampeonatoClubeResponseDTO toClubeDTO(CampeonatoClube clube, Map<Long, Long> atletasPorClube) {
        CampeonatoClubeResponseDTO dto = new CampeonatoClubeResponseDTO();
        dto.setCampeonatoClubeId(clube.getCampeonatoClubeId());
        dto.setClubeOrigemId(clube.getClubeOrigemId());
        dto.setNome(clube.getNome());
        dto.setSigla(clube.getSigla());
        dto.setPais(clube.getPais());
        dto.setRank(clube.getRank());
        dto.setCompetidorNumero(clube.getCompetidorNumero());
        dto.setCampeaoAnterior(clube.getCampeaoAnterior());
        dto.setExcluidoSorteio(clube.getExcluidoSorteio());
        dto.setEliminado(clube.getEliminado());
        dto.setJogos(clube.getJogos() == null ? 0 : clube.getJogos());
        dto.setGolsPro(clube.getGolsPro() == null ? 0 : clube.getGolsPro());
        dto.setGolsContra(clube.getGolsContra() == null ? 0 : clube.getGolsContra());
        dto.setSaldoGols(clube.getSaldoGols());
        if (atletasPorClube != null && clube.getCampeonatoClubeId() != null) {
            dto.setQuantidadeAtletas(atletasPorClube.getOrDefault(clube.getCampeonatoClubeId(), 0L));
        }
        return dto;
    }

    public static CampeonatoClubeResponseDTO toClubeDTO(CampeonatoClube clube) {
        return toClubeDTO(clube, null);
    }

    public static ClubeDisponivelDTO toClubeDisponivel(Clube clube) {
        return toClubeDisponivel(clube, false);
    }

    public static ClubeDisponivelDTO toClubeDisponivel(Clube clube, boolean protegido) {
        ClubeDisponivelDTO dto = new ClubeDisponivelDTO();
        dto.setClubeId(clube.getClubeId());
        dto.setNome(clube.getNome());
        dto.setSigla(clube.getSigla());
        dto.setPais(clube.getPais());
        dto.setRank(clube.getRank());
        dto.setProtegido(protegido);
        dto.setElegivelParaPote(!protegido);
        return dto;
    }
}
