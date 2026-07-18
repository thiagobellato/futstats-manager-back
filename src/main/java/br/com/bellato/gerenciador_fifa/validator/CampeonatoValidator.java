package br.com.bellato.gerenciador_fifa.validator;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoCriarRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClubeSelecionadoDTO;
import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.model.Clube;

public class CampeonatoValidator {

    private static final Set<Integer> QUANTIDADES_PERMITIDAS = Set.of(16, 32, 64);

    private CampeonatoValidator() {
    }

    public static void validarCriacao(CampeonatoCriarRequestDTO dto, Map<Long, Clube> clubesPorId) {
        validarCriacao(dto, clubesPorId, null);
    }

    public static void validarCriacao(CampeonatoCriarRequestDTO dto, Map<Long, Clube> clubesPorId,
            Map<ClubRank, Long> disponibilidadePorRank) {
        validarInformacoesGerais(dto);
        validarCompetidores(dto);
        validarCampeaoAnterior(dto, clubesPorId);
        validarDistribuicaoRanks(dto, disponibilidadePorRank);
        validarClubesSelecionados(dto, clubesPorId);
    }

    public static void validarInformacoesGerais(CampeonatoCriarRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new CampeonatoBusinessException("O nome do campeonato é obrigatório.");
        }
        if (dto.getQuantidadeClubes() == null || !QUANTIDADES_PERMITIDAS.contains(dto.getQuantidadeClubes())) {
            throw new CampeonatoBusinessException("A quantidade de clubes deve ser 16, 32 ou 64.");
        }
    }

    public static void validarCompetidores(CampeonatoCriarRequestDTO dto) {
        if (dto.getCompetidor1Nome() == null || dto.getCompetidor1Nome().isBlank()) {
            throw new CampeonatoBusinessException("O nome do Competidor 1 é obrigatório.");
        }
        if (dto.getCompetidor2Nome() == null || dto.getCompetidor2Nome().isBlank()) {
            throw new CampeonatoBusinessException("O nome do Competidor 2 é obrigatório.");
        }
        if (dto.getCompetidor1Nome().trim().equalsIgnoreCase(dto.getCompetidor2Nome().trim())) {
            throw new CampeonatoBusinessException("Os nomes dos competidores devem ser diferentes.");
        }
    }

    public static void validarCampeaoAnterior(CampeonatoCriarRequestDTO dto, Map<Long, Clube> clubesPorId) {
        if (Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior())) {
            if (dto.getCampeaoAnteriorCompetidor() == null
                    || (dto.getCampeaoAnteriorCompetidor() != 1 && dto.getCampeaoAnteriorCompetidor() != 2)) {
                throw new CampeonatoBusinessException("Selecione qual competidor foi o campeão anterior (1 ou 2).");
            }
            if (dto.getCampeaoAnteriorClubeId() == null) {
                throw new CampeonatoBusinessException("Selecione o clube campeão anterior.");
            }
            if (!clubesPorId.containsKey(dto.getCampeaoAnteriorClubeId())) {
                throw new CampeonatoBusinessException("O clube campeão anterior informado não existe.");
            }
        } else if (dto.getCampeaoAnteriorCompetidor() != null || dto.getCampeaoAnteriorClubeId() != null) {
            throw new CampeonatoBusinessException(
                    "Não informe campeão anterior quando o campeonato não possui campeão anterior.");
        }
    }

    public static void validarDistribuicaoRanks(CampeonatoCriarRequestDTO dto) {
        validarDistribuicaoRanks(dto, null);
    }

    public static void validarDistribuicaoRanks(CampeonatoCriarRequestDTO dto,
            Map<ClubRank, Long> disponibilidadePorRank) {
        Map<ClubRank, Integer> distribuicao = dto.getDistribuicaoRanks();
        if (distribuicao == null || distribuicao.isEmpty()) {
            throw new CampeonatoBusinessException("A distribuição de ranks é obrigatória.");
        }

        int soma = 0;
        for (ClubRank rank : ClubRank.values()) {
            Integer quantidade = distribuicao.get(rank);
            if (quantidade == null || quantidade < 0) {
                throw new CampeonatoBusinessException("Informe uma quantidade válida para o rank " + rank.getSigla() + ".");
            }
            soma += quantidade;

            if (disponibilidadePorRank != null) {
                long disponivel = disponibilidadePorRank.getOrDefault(rank, 0L);
                if (quantidade > disponivel) {
                    throw new CampeonatoBusinessException(
                            "Existem apenas " + disponivel + " clubes disponíveis no Rank " + rank.getSigla() + ".");
                }
            }
        }

        if (soma != dto.getQuantidadeClubes()) {
            throw new CampeonatoBusinessException(
                    "A soma dos ranks (" + soma + ") deve ser igual à quantidade de clubes ("
                            + dto.getQuantidadeClubes() + ").");
        }
    }

    public static void validarClubesSelecionados(CampeonatoCriarRequestDTO dto, Map<Long, Clube> clubesPorId) {
        List<ClubeSelecionadoDTO> selecionados = dto.getClubesSelecionados();
        if (selecionados == null || selecionados.isEmpty()) {
            throw new CampeonatoBusinessException("Selecione os clubes participantes.");
        }

        if (selecionados.size() != dto.getQuantidadeClubes()) {
            throw new CampeonatoBusinessException(
                    "A quantidade de clubes selecionados deve ser " + dto.getQuantidadeClubes() + ".");
        }

        Map<ClubRank, Integer> contagemPorRank = new EnumMap<>(ClubRank.class);
        for (ClubRank rank : ClubRank.values()) {
            contagemPorRank.put(rank, 0);
        }

        Set<Long> idsUnicos = new HashSet<>();
        boolean campeaoIncluido = false;

        for (ClubeSelecionadoDTO selecionado : selecionados) {
            if (selecionado.getClubeId() == null || selecionado.getRank() == null) {
                throw new CampeonatoBusinessException("Cada clube selecionado deve possuir ID e rank.");
            }
            if (!idsUnicos.add(selecionado.getClubeId())) {
                throw new CampeonatoBusinessException("O mesmo clube não pode ser selecionado mais de uma vez.");
            }
            if (!clubesPorId.containsKey(selecionado.getClubeId())) {
                throw new CampeonatoBusinessException("Clube com ID " + selecionado.getClubeId() + " não encontrado.");
            }
            contagemPorRank.merge(selecionado.getRank(), 1, Integer::sum);

            if (Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior())
                    && selecionado.getClubeId().equals(dto.getCampeaoAnteriorClubeId())) {
                campeaoIncluido = true;
            }
        }

        for (ClubRank rank : ClubRank.values()) {
            int esperado = dto.getDistribuicaoRanks().get(rank);
            int selecionado = contagemPorRank.get(rank);
            if (selecionado != esperado) {
                throw new CampeonatoBusinessException(
                        "O rank " + rank.getSigla() + " deve ter exatamente " + esperado
                                + " clubes, mas foram selecionados " + selecionado + ".");
            }
        }

        if (Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior()) && !campeaoIncluido) {
            throw new CampeonatoBusinessException("O clube campeão anterior deve estar entre os clubes selecionados.");
        }
    }
}
