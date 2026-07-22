package br.com.bellato.gerenciador_fifa.service.transferencia;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaAtletaRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Estratégia que altera entidades globais (fluxo já existente do sistema).
 */
@Component
public class GlobalTransferStrategy implements AthleteTransferStrategy {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private EstatisticaAtletaRepository estatisticaAtletaRepository;

    @Override
    public AthleteTransferCommand.Escopo getEscopo() {
        return AthleteTransferCommand.Escopo.GLOBAL;
    }

    @Override
    @Transactional
    public void transferir(AthleteTransferCommand command) {
        if (command.getAtletaGlobalId() == null || command.getNovoClubeGlobalId() == null) {
            throw new IllegalArgumentException("Informe o atleta e o novo clube para transferência global.");
        }

        Atleta atleta = atletaRepository.findById(command.getAtletaGlobalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Atleta não encontrado com ID: " + command.getAtletaGlobalId()));

        Clube novoClube = clubeRepository.findById(command.getNovoClubeGlobalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Clube não encontrado com ID: " + command.getNovoClubeGlobalId()));

        if (atleta.getClube() != null
                && Objects.equals(atleta.getClube().getClubeId(), novoClube.getClubeId())) {
            throw new IllegalArgumentException("O atleta já pertence a este clube.");
        }

        Optional<EstatisticaAtleta> estatisticaAtualOpt = estatisticaAtletaRepository
                .findByAtletaIdAndDataFimIsNull(command.getAtletaGlobalId());
        estatisticaAtualOpt.ifPresent(estatistica -> {
            estatistica.setDataFim(LocalDate.now());
            estatisticaAtletaRepository.save(estatistica);
        });

        EstatisticaAtleta novaEstatistica = new EstatisticaAtleta();
        novaEstatistica.setAtleta(atleta);
        novaEstatistica.setClube(novoClube);
        novaEstatistica.setDataInicio(LocalDate.now());
        novaEstatistica.setGols(0);
        novaEstatistica.setAssistencias(0);
        novaEstatistica.setCartaoAmarelo(0);
        novaEstatistica.setCartaoVermelho(0);
        novaEstatistica.setGolsContra(0);
        estatisticaAtletaRepository.save(novaEstatistica);

        atleta.setClube(novoClube);
        atletaRepository.save(atleta);
    }
}
