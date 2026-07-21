package br.com.bellato.gerenciador_fifa.service.transferencia;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fachada de domínio para transferência de atletas.
 * Delega a persistência à estratégia correspondente (global ou campeonato).
 */
@Service
public class AthleteTransferService {

    private final Map<AthleteTransferCommand.Escopo, AthleteTransferStrategy> strategies =
            new EnumMap<>(AthleteTransferCommand.Escopo.class);

    @Autowired
    public AthleteTransferService(List<AthleteTransferStrategy> strategyList) {
        for (AthleteTransferStrategy strategy : strategyList) {
            strategies.put(strategy.getEscopo(), strategy);
        }
    }

    @Transactional
    public void transferir(AthleteTransferCommand command) {
        if (command == null || command.getEscopo() == null) {
            throw new IllegalArgumentException("Informe o escopo da transferência.");
        }
        AthleteTransferStrategy strategy = strategies.get(command.getEscopo());
        if (strategy == null) {
            throw new IllegalStateException("Estratégia de transferência não encontrada: " + command.getEscopo());
        }
        strategy.transferir(command);
    }

    @Transactional
    public void transferirGlobal(Long atletaId, Long novoClubeId) {
        AthleteTransferCommand command = new AthleteTransferCommand();
        command.setEscopo(AthleteTransferCommand.Escopo.GLOBAL);
        command.setAtletaGlobalId(atletaId);
        command.setNovoClubeGlobalId(novoClubeId);
        transferir(command);
    }

    @Transactional
    public void transferirNoCampeonato(Long campeonatoId, Long campeonatoAtletaId, Long atletaGlobalId,
            Long novoCampeonatoClubeId) {
        AthleteTransferCommand command = new AthleteTransferCommand();
        command.setEscopo(AthleteTransferCommand.Escopo.CAMPEONATO);
        command.setCampeonatoId(campeonatoId);
        command.setCampeonatoAtletaId(campeonatoAtletaId);
        command.setAtletaGlobalId(atletaGlobalId);
        command.setNovoCampeonatoClubeId(novoCampeonatoClubeId);
        transferir(command);
    }
}
