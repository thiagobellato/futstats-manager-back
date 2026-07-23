package br.com.bellato.gerenciador_fifa.validator;

import java.util.List;
import java.util.Objects;

import br.com.bellato.gerenciador_fifa.dto.campeonato.PartidaEventoRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RegistrarPartidaRequestDTO;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.service.transferencia.CampeonatoAtletaIdentidade;

public final class PartidaRegistroValidator {

    private PartidaRegistroValidator() {
    }

    public static void validarRequestBasico(RegistrarPartidaRequestDTO request) {
        if (request == null) {
            throw new CampeonatoBusinessException("Informe o resultado da partida.");
        }
        if (request.getGolsMandante() == null || request.getGolsVisitante() == null) {
            throw new CampeonatoBusinessException("Informe o placar completo da partida.");
        }
        if (request.getGolsMandante() < 0 || request.getGolsVisitante() < 0) {
            throw new CampeonatoBusinessException("O placar não pode conter valores negativos.");
        }
    }

    /**
     * Atletas suspensos por expulsão não podem participar de eventos da partida.
     */
    public static void validarAtletasSuspensos(
            List<CampeonatoAtleta> atletasResolvidos,
            java.util.Set<String> identidadesSuspensas) {

        if (identidadesSuspensas == null || identidadesSuspensas.isEmpty()) {
            return;
        }
        for (CampeonatoAtleta atleta : atletasResolvidos) {
            if (atleta == null) {
                continue;
            }
            String identidade = CampeonatoAtletaIdentidade.garantir(atleta);
            if (identidadesSuspensas.contains(identidade)) {
                throw new CampeonatoBusinessException(
                        "O atleta " + nomeAtleta(atleta)
                                + " está suspenso por expulsão e não pode ser utilizado nesta partida.");
            }
        }
    }

    /**
     * Regra oficial: gol contra não gera assistência.
     * Máximo de assistências = gols marcados pela equipe − gols contra que beneficiaram essa equipe.
     */
    public static int calcularMaximoAssistencias(int golsMarcadosEquipe, int golsContraBeneficiandoEquipe) {
        return Math.max(0, golsMarcadosEquipe - golsContraBeneficiandoEquipe);
    }

    public static void validarEventosContraPlacar(
            CampeonatoPartida partida,
            RegistrarPartidaRequestDTO request,
            List<CampeonatoAtleta> atletasResolvidos) {

        Long mandanteId = partida.getClubeMandante().getCampeonatoClubeId();
        Long visitanteId = partida.getClubeVisitante().getCampeonatoClubeId();

        int golsCreditadosMandante = 0;
        int golsCreditadosVisitante = 0;
        int golsContraBeneficiandoMandante = 0;
        int golsContraBeneficiandoVisitante = 0;
        int assistenciasMandante = 0;
        int assistenciasVisitante = 0;

        List<PartidaEventoRequestDTO> eventos = request.getEventos() == null
                ? List.of()
                : request.getEventos();

        if (eventos.size() != atletasResolvidos.size()) {
            throw new CampeonatoBusinessException("Falha ao resolver atletas dos eventos da partida.");
        }

        for (int i = 0; i < eventos.size(); i++) {
            PartidaEventoRequestDTO evento = eventos.get(i);
            CampeonatoAtleta atleta = atletasResolvidos.get(i);

            if (evento.getTipo() == null) {
                throw new CampeonatoBusinessException("Todo evento precisa ter um tipo definido.");
            }
            if (evento.getCampeonatoAtletaId() == null) {
                throw new CampeonatoBusinessException("Todo evento precisa estar vinculado a um atleta.");
            }
            if (atleta == null || atleta.getCampeonatoClube() == null) {
                throw new CampeonatoBusinessException("Atleta do evento não encontrado no campeonato.");
            }

            Long clubeAtletaId = atleta.getCampeonatoClube().getCampeonatoClubeId();
            boolean doMandante = Objects.equals(clubeAtletaId, mandanteId);
            boolean doVisitante = Objects.equals(clubeAtletaId, visitanteId);

            if (!doMandante && !doVisitante) {
                throw new CampeonatoBusinessException(
                        "O atleta " + nomeAtleta(atleta) + " não pertence aos clubes desta partida.");
            }

            switch (evento.getTipo()) {
                case GOL -> {
                    if (doMandante) {
                        golsCreditadosMandante++;
                    } else {
                        golsCreditadosVisitante++;
                    }
                }
                case GOL_CONTRA -> {
                    // Gol contra credita o placar ao adversário e não gera assistência
                    if (doMandante) {
                        golsCreditadosVisitante++;
                        golsContraBeneficiandoVisitante++;
                    } else {
                        golsCreditadosMandante++;
                        golsContraBeneficiandoMandante++;
                    }
                }
                case ASSISTENCIA -> {
                    if (doMandante) {
                        assistenciasMandante++;
                    } else {
                        assistenciasVisitante++;
                    }
                }
                case CARTAO_AMARELO, CARTAO_VERMELHO -> {
                    // Cartões não afetam placar nem teto de assistências
                }
                default -> throw new CampeonatoBusinessException("Tipo de evento não suportado: " + evento.getTipo());
            }
        }

        if (golsCreditadosMandante != request.getGolsMandante()) {
            throw new CampeonatoBusinessException(String.format(
                    "Gols registrados do mandante (%d) não coincidem com o placar informado (%d). "
                            + "Inclua gols normais e gols contra do adversário.",
                    golsCreditadosMandante, request.getGolsMandante()));
        }
        if (golsCreditadosVisitante != request.getGolsVisitante()) {
            throw new CampeonatoBusinessException(String.format(
                    "Gols registrados do visitante (%d) não coincidem com o placar informado (%d). "
                            + "Inclua gols normais e gols contra do adversário.",
                    golsCreditadosVisitante, request.getGolsVisitante()));
        }

        int maxAssMandante = calcularMaximoAssistencias(
                request.getGolsMandante(), golsContraBeneficiandoMandante);
        int maxAssVisitante = calcularMaximoAssistencias(
                request.getGolsVisitante(), golsContraBeneficiandoVisitante);

        if (assistenciasMandante > maxAssMandante) {
            throw new CampeonatoBusinessException(String.format(
                    "Assistências do mandante (%d) excedem o máximo permitido (%d). "
                            + "Gols contra não geram assistência.",
                    assistenciasMandante, maxAssMandante));
        }
        if (assistenciasVisitante > maxAssVisitante) {
            throw new CampeonatoBusinessException(String.format(
                    "Assistências do visitante (%d) excedem o máximo permitido (%d). "
                            + "Gols contra não geram assistência.",
                    assistenciasVisitante, maxAssVisitante));
        }
    }

    public static CampeonatoClube determinarVencedor(
            CampeonatoPartida partida,
            RegistrarPartidaRequestDTO request) {

        int golsM = request.getGolsMandante();
        int golsV = request.getGolsVisitante();

        if (golsM > golsV) {
            validarSemPenaltisDesnecessarios(request);
            return partida.getClubeMandante();
        }
        if (golsV > golsM) {
            validarSemPenaltisDesnecessarios(request);
            return partida.getClubeVisitante();
        }

        boolean disputou = Boolean.TRUE.equals(request.getDisputouPenaltis());
        if (!disputou) {
            throw new CampeonatoBusinessException(
                    "Empate no tempo regulamentar. Informe a disputa por pênaltis para definir o classificado.");
        }
        if (request.getPenaltisMandante() == null || request.getPenaltisVisitante() == null) {
            throw new CampeonatoBusinessException("Informe o placar completo da disputa por pênaltis.");
        }
        if (request.getPenaltisMandante() < 0 || request.getPenaltisVisitante() < 0) {
            throw new CampeonatoBusinessException("O placar de pênaltis não pode conter valores negativos.");
        }
        if (Objects.equals(request.getPenaltisMandante(), request.getPenaltisVisitante())) {
            throw new CampeonatoBusinessException(
                    "A disputa por pênaltis precisa ter um vencedor (placares iguais não são permitidos).");
        }

        return request.getPenaltisMandante() > request.getPenaltisVisitante()
                ? partida.getClubeMandante()
                : partida.getClubeVisitante();
    }

    private static void validarSemPenaltisDesnecessarios(RegistrarPartidaRequestDTO request) {
        if (Boolean.TRUE.equals(request.getDisputouPenaltis())) {
            throw new CampeonatoBusinessException(
                    "Disputa por pênaltis só é permitida quando o placar do tempo regulamentar estiver empatado.");
        }
    }

    private static String nomeAtleta(CampeonatoAtleta atleta) {
        if (atleta.getSobrenome() == null || atleta.getSobrenome().isBlank()) {
            return atleta.getNome();
        }
        return atleta.getNome() + " " + atleta.getSobrenome();
    }
}
