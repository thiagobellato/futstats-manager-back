package br.com.bellato.gerenciador_fifa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.com.bellato.gerenciador_fifa.service.CampeonatoService;

/**
 * Backfill de CampeonatoParticipante para campeonatos legados (modo livre).
 */
@Component
@Order(20)
public class CampeonatoParticipanteBackfill implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CampeonatoParticipanteBackfill.class);

    private final CampeonatoService campeonatoService;

    public CampeonatoParticipanteBackfill(CampeonatoService campeonatoService) {
        this.campeonatoService = campeonatoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            int criados = campeonatoService.backfillParticipantesLegados();
            if (criados > 0) {
                log.info("CampeonatoParticipante: backfill criou {} participantes legados", criados);
            }
        } catch (Exception ex) {
            log.warn("CampeonatoParticipante: backfill ignorado ({})", ex.getMessage());
        }
    }
}
