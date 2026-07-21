package br.com.bellato.gerenciador_fifa.service.transferencia;

import java.util.UUID;

import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;

/**
 * Identidade estável do atleta dentro do campeonato (agrupa vínculos após transferências).
 */
public final class CampeonatoAtletaIdentidade {

    private CampeonatoAtletaIdentidade() {
    }

    public static String paraAtletaGlobal(Long atletaOrigemId) {
        return "G-" + atletaOrigemId;
    }

    public static String novaLocal() {
        return "L-" + UUID.randomUUID();
    }

    public static String garantir(CampeonatoAtleta atleta) {
        if (atleta.getIdentidade() != null && !atleta.getIdentidade().isBlank()) {
            return atleta.getIdentidade();
        }
        if (atleta.getAtletaOrigemId() != null) {
            String identidade = paraAtletaGlobal(atleta.getAtletaOrigemId());
            atleta.setIdentidade(identidade);
            return identidade;
        }
        if (atleta.getCampeonatoAtletaId() != null) {
            String identidade = "L-" + atleta.getCampeonatoAtletaId();
            atleta.setIdentidade(identidade);
            return identidade;
        }
        String identidade = novaLocal();
        atleta.setIdentidade(identidade);
        return identidade;
    }
}
