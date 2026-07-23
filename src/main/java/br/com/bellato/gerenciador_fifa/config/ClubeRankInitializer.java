package br.com.bellato.gerenciador_fifa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.com.bellato.gerenciador_fifa.service.ClubeRankService;

@Component
@Order(2)
public class ClubeRankInitializer implements ApplicationRunner {

    @Autowired
    private ClubeRankService clubeRankService;

    @Override
    public void run(ApplicationArguments args) {
        clubeRankService.inicializarRanksPendentes();
    }
}
