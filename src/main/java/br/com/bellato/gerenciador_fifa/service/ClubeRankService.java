package br.com.bellato.gerenciador_fifa.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResumoRankResponseDTO;
import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaClubeRepository;

@Service
public class ClubeRankService {

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private EstatisticaClubeRepository estatisticaClubeRepository;

    public ClubeResumoRankResponseDTO obterResumoRanks() {
        Map<ClubRank, Long> quantidadePorRank = new EnumMap<>(ClubRank.class);
        for (ClubRank rank : ClubRank.values()) {
            quantidadePorRank.put(rank, 0L);
        }

        for (Object[] registro : estatisticaClubeRepository.contarAgrupadoPorRank()) {
            ClubRank rank = (ClubRank) registro[0];
            Long quantidade = (Long) registro[1];
            quantidadePorRank.put(rank, quantidade);
        }

        long total = quantidadePorRank.values().stream().mapToLong(Long::longValue).sum();

        ClubeResumoRankResponseDTO resumo = new ClubeResumoRankResponseDTO();
        resumo.setQuantidadePorRank(quantidadePorRank);
        resumo.setTotalClubes(total);
        return resumo;
    }

    public Map<ClubRank, Long> obterQuantidadePorRank() {
        return obterResumoRanks().getQuantidadePorRank();
    }

    @Transactional
    public void inicializarRanksPendentes() {
        atribuirRanksEquilibrados();
    }

    @Transactional
    public void atribuirRankSeNecessario(Clube clube) {
        anexarEstatisticaExistente(clube);
        if (clube.getRank() != null) {
            return;
        }
        clube.setRank(encontrarRankComMenosClubes());
        persistirSomenteViaClube(clube);
    }

    @Transactional
    public void atribuirRanksSeNecessario(List<Clube> clubes) {
        List<Clube> pendentes = new ArrayList<>();
        for (Clube clube : clubes) {
            anexarEstatisticaExistente(clube);
            if (clube.getRank() == null) {
                pendentes.add(clube);
            }
        }
        if (pendentes.isEmpty()) {
            return;
        }
        distribuirEquilibradamente(pendentes);
        for (Clube clube : pendentes) {
            persistirSomenteViaClube(clube);
        }
    }

    private void atribuirRanksEquilibrados() {
        List<Clube> semRank = clubeRepository.findClubesSemRank();
        if (semRank.isEmpty()) {
            return;
        }
        for (Clube clube : semRank) {
            anexarEstatisticaExistente(clube);
        }
        List<Clube> aindaSemRank = semRank.stream()
                .filter(c -> c.getRank() == null)
                .toList();
        if (aindaSemRank.isEmpty()) {
            return;
        }
        distribuirEquilibradamente(aindaSemRank);
        for (Clube clube : aindaSemRank) {
            persistirSomenteViaClube(clube);
        }
    }

    /**
     * Se já existe linha 1:1 no banco, reutiliza (evita UK em clube_id).
     */
    private void anexarEstatisticaExistente(Clube clube) {
        if (clube.getEstatistica() != null && clube.getEstatistica().getId() != null) {
            return;
        }
        estatisticaClubeRepository.findByClubeClubeId(clube.getClubeId()).ifPresent(existente -> {
            ClubRank rankPendente = clube.getEstatistica() != null ? clube.getEstatistica().getRank() : null;
            clube.setEstatistica(existente);
            if (existente.getRank() == null && rankPendente != null) {
                existente.setRank(rankPendente);
            }
        });
    }

    /** Cascade ALL em Clube.estatistica — um único save evita insert duplicado. */
    private void persistirSomenteViaClube(Clube clube) {
        if (clube.getEstatistica() == null) {
            return;
        }
        clubeRepository.save(clube);
    }

    private void distribuirEquilibradamente(List<Clube> clubes) {
        ClubRank[] ranks = ClubRank.values();
        int total = clubes.size();
        int base = total / ranks.length;
        int resto = total % ranks.length;

        int indice = 0;
        for (int r = 0; r < ranks.length; r++) {
            int quantidade = base + (r < resto ? 1 : 0);
            for (int i = 0; i < quantidade && indice < total; i++) {
                clubes.get(indice).setRank(ranks[r]);
                indice++;
            }
        }
    }

    private ClubRank encontrarRankComMenosClubes() {
        Map<ClubRank, Long> contagem = obterQuantidadePorRank();

        ClubRank menorRank = ClubRank.S;
        long menorQuantidade = Long.MAX_VALUE;
        for (ClubRank rank : ClubRank.values()) {
            long quantidade = contagem.getOrDefault(rank, 0L);
            if (quantidade < menorQuantidade) {
                menorQuantidade = quantidade;
                menorRank = rank;
            }
        }
        return menorRank;
    }
}
