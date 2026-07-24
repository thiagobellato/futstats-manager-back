package br.com.bellato.gerenciador_fifa.validator;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public static int calcularVagasCampeaoProtegido(Boolean possuiCampeaoAnterior) {
        return Boolean.TRUE.equals(possuiCampeaoAnterior) ? 1 : 0;
    }

    public static int calcularVagasParaSelecao(Integer quantidadeClubes, Boolean possuiCampeaoAnterior) {
        if (quantidadeClubes == null) {
            return 0;
        }
        return quantidadeClubes - calcularVagasCampeaoProtegido(possuiCampeaoAnterior);
    }

    public static void validarCriacao(CampeonatoCriarRequestDTO dto, Map<Long, Clube> clubesPorId) {
        validarCriacao(dto, clubesPorId, null);
    }

    public static void validarCriacao(CampeonatoCriarRequestDTO dto, Map<Long, Clube> clubesPorId,
            Map<ClubRank, Long> disponibilidadePorRank) {
        validarInformacoesGerais(dto);
        validarCompetidores(dto);
        validarCampeaoAnterior(dto, clubesPorId);
        validarDistribuicaoRanks(dto, disponibilidadePorRank, clubesPorId);
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
        if (Boolean.TRUE.equals(dto.getAutenticado())) {
            if (dto.getCompetidor1UserId() == null) {
                throw new CampeonatoBusinessException(
                        "Em campeonato autenticado, o Participante 1 deve ser o usuário logado.");
            }
            if (dto.getCompetidor2UserId() == null) {
                throw new CampeonatoBusinessException(
                        "Em campeonato autenticado, selecione o adversário (Participante 2).");
            }
            if (dto.getCompetidor1UserId().equals(dto.getCompetidor2UserId())) {
                throw new CampeonatoBusinessException(
                        "Não é permitido selecionar a si mesmo como adversário.");
            }
            return;
        }

        if (dto.getCompetidor1Nome() == null || dto.getCompetidor1Nome().isBlank()) {
            throw new CampeonatoBusinessException("O nome do Competidor 1 é obrigatório.");
        }
        if (dto.getCompetidor2Nome() == null || dto.getCompetidor2Nome().isBlank()) {
            throw new CampeonatoBusinessException("O nome do Competidor 2 é obrigatório.");
        }
        if (dto.getCompetidor1Nome().trim().equalsIgnoreCase(dto.getCompetidor2Nome().trim())) {
            throw new CampeonatoBusinessException("Os nomes dos competidores devem ser diferentes.");
        }
        if (dto.getCompetidor1UserId() != null || dto.getCompetidor2UserId() != null) {
            throw new CampeonatoBusinessException(
                    "Não informe usuários em campeonato livre.");
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
        validarDistribuicaoRanks(dto, null, null);
    }

    public static void validarDistribuicaoRanks(CampeonatoCriarRequestDTO dto,
            Map<ClubRank, Long> disponibilidadePorRank) {
        validarDistribuicaoRanks(dto, disponibilidadePorRank, null);
    }

    public static void validarDistribuicaoRanks(CampeonatoCriarRequestDTO dto,
            Map<ClubRank, Long> disponibilidadePorRank, Map<Long, Clube> clubesPorId) {
        Map<ClubRank, Integer> distribuicao = dto.getDistribuicaoRanks();
        if (distribuicao == null || distribuicao.isEmpty()) {
            throw new CampeonatoBusinessException("A distribuição de ranks é obrigatória.");
        }

        int vagasParaSelecao = calcularVagasParaSelecao(dto.getQuantidadeClubes(), dto.getPossuiCampeaoAnterior());
        Map<ClubRank, Long> disponibilidadeEfetiva = ajustarDisponibilidadeExcluindoCampeao(
                disponibilidadePorRank, dto, clubesPorId);

        int soma = 0;
        for (ClubRank rank : ClubRank.values()) {
            Integer quantidade = distribuicao.get(rank);
            if (quantidade == null || quantidade < 0) {
                throw new CampeonatoBusinessException("Informe uma quantidade válida para o rank " + rank.getSigla() + ".");
            }
            soma += quantidade;

            if (disponibilidadeEfetiva != null) {
                long disponivel = disponibilidadeEfetiva.getOrDefault(rank, 0L);
                if (quantidade > disponivel) {
                    throw new CampeonatoBusinessException(
                            "Existem apenas " + disponivel + " clubes disponíveis no Rank " + rank.getSigla()
                                    + " para seleção"
                                    + (Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior())
                                            ? " (desconsiderando o campeão protegido)."
                                            : "."));
                }
            }
        }

        if (soma != vagasParaSelecao) {
            String detalheCampeao = Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior())
                    ? " O campeão protegido já ocupa 1 vaga, restando " + vagasParaSelecao + " para distribuição."
                    : "";
            throw new CampeonatoBusinessException(
                    "A soma dos ranks (" + soma + ") deve ser igual às vagas para seleção ("
                            + vagasParaSelecao + ")." + detalheCampeao);
        }
    }

    public static void validarClubesSelecionados(CampeonatoCriarRequestDTO dto, Map<Long, Clube> clubesPorId) {
        List<ClubeSelecionadoDTO> selecionados = dto.getClubesSelecionados();
        if (selecionados == null || selecionados.isEmpty()) {
            throw new CampeonatoBusinessException("Selecione os clubes participantes.");
        }

        if (selecionados.size() != dto.getQuantidadeClubes()) {
            throw new CampeonatoBusinessException(
                    "A quantidade total de clubes participantes deve ser " + dto.getQuantidadeClubes() + ".");
        }

        Map<ClubRank, Integer> contagemPorRank = new EnumMap<>(ClubRank.class);
        for (ClubRank rank : ClubRank.values()) {
            contagemPorRank.put(rank, 0);
        }

        Set<Long> idsUnicos = new HashSet<>();
        boolean campeaoIncluido = false;
        Long campeaoId = Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior())
                ? dto.getCampeaoAnteriorClubeId()
                : null;

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

            boolean ehCampeao = campeaoId != null && Objects.equals(selecionado.getClubeId(), campeaoId);
            if (ehCampeao) {
                campeaoIncluido = true;
            } else {
                contagemPorRank.merge(selecionado.getRank(), 1, Integer::sum);
            }
        }

        for (ClubRank rank : ClubRank.values()) {
            int esperado = dto.getDistribuicaoRanks().get(rank);
            int selecionado = contagemPorRank.get(rank);
            if (selecionado != esperado) {
                throw new CampeonatoBusinessException(
                        "O rank " + rank.getSigla() + " deve ter exatamente " + esperado
                                + " clubes selecionados (além do campeão protegido, se houver), mas foram selecionados "
                                + selecionado + ".");
            }
        }

        if (Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior()) && !campeaoIncluido) {
            throw new CampeonatoBusinessException("O clube campeão anterior deve estar entre os clubes selecionados.");
        }
    }

    public static Map<ClubRank, Long> ajustarDisponibilidadeExcluindoCampeao(
            Map<ClubRank, Long> disponibilidadePorRank,
            CampeonatoCriarRequestDTO dto,
            Map<Long, Clube> clubesPorId) {
        if (disponibilidadePorRank == null) {
            return null;
        }

        Map<ClubRank, Long> ajustada = new EnumMap<>(ClubRank.class);
        ajustada.putAll(disponibilidadePorRank);

        if (!Boolean.TRUE.equals(dto.getPossuiCampeaoAnterior()) || dto.getCampeaoAnteriorClubeId() == null
                || clubesPorId == null) {
            return ajustada;
        }

        Clube campeao = clubesPorId.get(dto.getCampeaoAnteriorClubeId());
        if (campeao != null && campeao.getRank() != null) {
            long atual = ajustada.getOrDefault(campeao.getRank(), 0L);
            ajustada.put(campeao.getRank(), Math.max(0L, atual - 1));
        }
        return ajustada;
    }
}
