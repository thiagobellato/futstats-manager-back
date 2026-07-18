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

@Service
public class ClubeRankService {

    @Autowired
    private ClubeRepository clubeRepository;

    public ClubeResumoRankResponseDTO obterResumoRanks() {
        Map<ClubRank, Long> quantidadePorRank = new EnumMap<>(ClubRank.class);
        for (ClubRank rank : ClubRank.values()) {
            quantidadePorRank.put(rank, 0L);
        }

        for (Object[] registro : clubeRepository.contarAgrupadoPorRank()) {
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
        if (clube.getRank() != null) {
            return;
        }
        clube.setRank(encontrarRankComMenosClubes());
        clubeRepository.save(clube);
    }

    @Transactional
    public void atribuirRanksSeNecessario(List<Clube> clubes) {
        List<Clube> pendentes = new ArrayList<>();
        for (Clube clube : clubes) {
            if (clube.getRank() == null) {
                pendentes.add(clube);
            }
        }
        if (pendentes.isEmpty()) {
            return;
        }
        distribuirEquilibradamente(pendentes);
        clubeRepository.saveAll(pendentes);
    }

    private void atribuirRanksEquilibrados() {
        List<Clube> semRank = clubeRepository.findByRankIsNull();
        if (semRank.isEmpty()) {
            return;
        }
        distribuirEquilibradamente(semRank);
        clubeRepository.saveAll(semRank);
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
