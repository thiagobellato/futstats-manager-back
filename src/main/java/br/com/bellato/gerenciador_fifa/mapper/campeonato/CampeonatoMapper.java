package br.com.bellato.gerenciador_fifa.mapper.campeonato;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoClubeResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClubeDisponivelDTO;
import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoDistribuicaoRank;
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

        if (campeonato.getClubes() != null) {
            String campeaoNome = campeonato.getClubes().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getCampeaoAnterior()))
                    .map(CampeonatoClube::getNome)
                    .findFirst()
                    .orElse(null);
            dto.setCampeaoAnteriorClubeNome(campeaoNome);

            dto.setClubes(campeonato.getClubes().stream()
                    .map(clube -> toClubeDTO(clube, atletasPorClube))
                    .collect(Collectors.toList()));
        }

        if (campeonato.getDistribuicaoRanks() != null) {
            Map<ClubRank, Integer> distribuicao = new EnumMap<>(ClubRank.class);
            for (CampeonatoDistribuicaoRank item : campeonato.getDistribuicaoRanks()) {
                distribuicao.put(item.getRank(), item.getQuantidade());
            }
            dto.setDistribuicaoRanks(distribuicao);
        }

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
        if (atletasPorClube != null && clube.getCampeonatoClubeId() != null) {
            dto.setQuantidadeAtletas(atletasPorClube.getOrDefault(clube.getCampeonatoClubeId(), 0L));
        }
        return dto;
    }

    public static CampeonatoClubeResponseDTO toClubeDTO(CampeonatoClube clube) {
        return toClubeDTO(clube, null);
    }

    public static ClubeDisponivelDTO toClubeDisponivel(Clube clube) {
        ClubeDisponivelDTO dto = new ClubeDisponivelDTO();
        dto.setClubeId(clube.getClubeId());
        dto.setNome(clube.getNome());
        dto.setSigla(clube.getSigla());
        dto.setPais(clube.getPais());
        dto.setRank(clube.getRank());
        return dto;
    }
}
