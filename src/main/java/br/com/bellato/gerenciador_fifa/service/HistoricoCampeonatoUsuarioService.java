package br.com.bellato.gerenciador_fifa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoParticipante;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.EstatisticaUsuario;
import br.com.bellato.gerenciador_fifa.model.HistoricoConfrontoUsuario;
import br.com.bellato.gerenciador_fifa.model.RivalidadeUsuario;
import br.com.bellato.gerenciador_fifa.model.UsoClubeUsuario;
import br.com.bellato.gerenciador_fifa.model.User;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoParticipanteRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaUsuarioRepository;
import br.com.bellato.gerenciador_fifa.repository.HistoricoConfrontoUsuarioRepository;
import br.com.bellato.gerenciador_fifa.repository.RivalidadeUsuarioRepository;
import br.com.bellato.gerenciador_fifa.repository.UsoClubeUsuarioRepository;

/**
 * Camada adicional de histórico entre usuários autenticados.
 * Chamada apenas após a finalização padrão — não altera Hall da Fama nem sync global.
 */
@Service
public class HistoricoCampeonatoUsuarioService {

    private static final Logger log = LoggerFactory.getLogger(HistoricoCampeonatoUsuarioService.class);

    @Autowired
    private CampeonatoParticipanteRepository participanteRepository;

    @Autowired
    private HistoricoConfrontoUsuarioRepository historicoConfrontoRepository;

    @Autowired
    private EstatisticaUsuarioRepository estatisticaUsuarioRepository;

    @Autowired
    private UsoClubeUsuarioRepository usoClubeUsuarioRepository;

    @Autowired
    private RivalidadeUsuarioRepository rivalidadeUsuarioRepository;

    @Autowired
    private ClubeRepository clubeRepository;

    @Transactional
    public void registrarAposFinalizacao(Campeonato campeonato, Clube campeaoGlobal) {
        if (!Boolean.TRUE.equals(campeonato.getAutenticado())) {
            return;
        }
        if (historicoConfrontoRepository.existsByCampeonatoCampeonatoId(campeonato.getCampeonatoId())) {
            log.info("Histórico de confronto já existe para campeonato {} — ignorando", campeonato.getCampeonatoId());
            return;
        }

        List<CampeonatoParticipante> participantes = participanteRepository
                .findByCampeonatoIdComUser(campeonato.getCampeonatoId());
        CampeonatoParticipante part1 = participantes.stream().filter(p -> Integer.valueOf(1).equals(p.getSide()))
                .findFirst().orElse(null);
        CampeonatoParticipante part2 = participantes.stream().filter(p -> Integer.valueOf(2).equals(p.getSide()))
                .findFirst().orElse(null);

        if (part1 == null || part2 == null || part1.getUser() == null || part2.getUser() == null) {
            log.warn("Campeonato autenticado {} sem participantes/users válidos — histórico ignorado",
                    campeonato.getCampeonatoId());
            return;
        }

        MatchAgg agg = agregarPartidasCruzadas(campeonato);

        CampeonatoParticipante winner = null;
        Integer campeaoSide = campeonato.getCampeaoCompetidor();
        if (Integer.valueOf(1).equals(campeaoSide)) {
            winner = part1;
        } else if (Integer.valueOf(2).equals(campeaoSide)) {
            winner = part2;
        }

        LocalDateTime finishedAt = campeonato.getDataFinalizacao() != null
                ? campeonato.getDataFinalizacao()
                : LocalDateTime.now();

        HistoricoConfrontoUsuario historico = new HistoricoConfrontoUsuario();
        historico.setCampeonato(campeonato);
        historico.setParticipanteA(part1);
        historico.setParticipanteB(part2);
        historico.setVencedor(winner);
        historico.setClubeCampeao(campeaoGlobal);
        historico.setNomeCampeonato(campeonato.getNome());
        historico.setDataFinalizacao(finishedAt);
        historico.setPartidasDisputadas(agg.matchesPlayed);
        historico.setGolsParticipanteA(agg.goalsSide1);
        historico.setGolsParticipanteB(agg.goalsSide2);
        if (agg.goleadaSide1.margem >= agg.goleadaSide2.margem && agg.goleadaSide1.margem > 0) {
            historico.setMaiorGoleadaGolsA(agg.goleadaSide1.golsPro);
            historico.setMaiorGoleadaGolsB(agg.goleadaSide1.golsContra);
        } else if (agg.goleadaSide2.margem > 0) {
            historico.setMaiorGoleadaGolsA(agg.goleadaSide2.golsContra);
            historico.setMaiorGoleadaGolsB(agg.goleadaSide2.golsPro);
        }
        historicoConfrontoRepository.save(historico);

        atualizarEstatisticasUsuario(part1.getUser(), 1, campeaoSide, agg, campeonato, campeaoGlobal, finishedAt);
        atualizarEstatisticasUsuario(part2.getUser(), 2, campeaoSide, agg, campeonato, campeaoGlobal, finishedAt);
        atualizarRivalidade(part1.getUser(), part2.getUser(), winner != null ? winner.getUser() : null,
                agg, finishedAt, campeonato.getNome());

        log.info("Histórico de usuários registrado para campeonato autenticado {}", campeonato.getCampeonatoId());
    }

    private MatchAgg agregarPartidasCruzadas(Campeonato campeonato) {
        MatchAgg agg = new MatchAgg();
        List<CampeonatoPartida> ordenadas = listarPartidasFinalizadasOrdenadas(campeonato);

        for (CampeonatoPartida partida : ordenadas) {
            CampeonatoClube mandante = partida.getClubeMandante();
            CampeonatoClube visitante = partida.getClubeVisitante();
            if (mandante == null || visitante == null) {
                continue;
            }
            Integer sideM = mandante.getCompetidorNumero();
            Integer sideV = visitante.getCompetidorNumero();
            if (sideM == null || sideV == null || sideM.equals(sideV)) {
                continue;
            }

            int golsM = partida.getGolsMandante() != null ? partida.getGolsMandante() : 0;
            int golsV = partida.getGolsVisitante() != null ? partida.getGolsVisitante() : 0;

            agg.matchesPlayed++;

            int golsSide1;
            int golsSide2;
            String clubeSide1;
            String clubeSide2;

            if (Integer.valueOf(1).equals(sideM)) {
                golsSide1 = golsM;
                golsSide2 = golsV;
                clubeSide1 = mandante.getNome();
                clubeSide2 = visitante.getNome();
            } else {
                golsSide1 = golsV;
                golsSide2 = golsM;
                clubeSide1 = visitante.getNome();
                clubeSide2 = mandante.getNome();
            }

            agg.goalsSide1 += golsSide1;
            agg.goalsSide2 += golsSide2;

            // Rivalidade / stats autenticadas: resultado do tempo regulamentar.
            // Pênaltis definem avanço no campeonato, mas não alteram V/E/D aqui.
            if (golsSide1 == golsSide2) {
                agg.draws++;
                agg.resultadosOrdenados.add(0);
            } else if (golsSide1 > golsSide2) {
                agg.winsSide1++;
                agg.resultadosOrdenados.add(1);
                considerarGoleada(agg.goleadaSide1, golsSide1, golsSide2, clubeSide1, clubeSide2);
            } else {
                agg.winsSide2++;
                agg.resultadosOrdenados.add(2);
                considerarGoleada(agg.goleadaSide2, golsSide2, golsSide1, clubeSide2, clubeSide1);
            }
        }
        return agg;
    }

    private static void considerarGoleada(GoleadaCand cand, int golsPro, int golsContra,
            String clubePro, String clubeContra) {
        int margem = golsPro - golsContra;
        if (margem <= 0) {
            return;
        }
        if (margem > cand.margem || (margem == cand.margem && golsPro > cand.golsPro)) {
            cand.margem = margem;
            cand.golsPro = golsPro;
            cand.golsContra = golsContra;
            cand.clubePro = clubePro;
            cand.clubeContra = clubeContra;
        }
    }

    private List<CampeonatoPartida> listarPartidasFinalizadasOrdenadas(Campeonato campeonato) {
        List<CampeonatoPartida> lista = new ArrayList<>();
        if (campeonato.getRodadas() == null) {
            return lista;
        }
        List<CampeonatoRodada> rodadas = new ArrayList<>(campeonato.getRodadas());
        rodadas.sort(Comparator.comparing(r -> r.getNumeroRodada() != null ? r.getNumeroRodada() : 0));
        for (CampeonatoRodada rodada : rodadas) {
            if (rodada.getPartidas() == null) {
                continue;
            }
            List<CampeonatoPartida> partidas = new ArrayList<>(rodada.getPartidas());
            partidas.sort(Comparator.comparing(p -> p.getOrdem() != null ? p.getOrdem() : 0));
            for (CampeonatoPartida partida : partidas) {
                if (partida.getStatus() == StatusPartida.FINALIZADA) {
                    lista.add(partida);
                }
            }
        }
        return lista;
    }

    private void atualizarEstatisticasUsuario(User user, int side, Integer campeaoSide, MatchAgg agg,
            Campeonato campeonato, Clube campeaoGlobal, LocalDateTime finishedAt) {
        EstatisticaUsuario stats = estatisticaUsuarioRepository.findByUsuarioUserId(user.getUserId())
                .orElseGet(() -> {
                    EstatisticaUsuario s = new EstatisticaUsuario();
                    s.setUsuario(user);
                    return s;
                });

        stats.setCampeonatosDisputados(stats.getCampeonatosDisputados() + 1);
        if (campeaoSide != null && campeaoSide == side) {
            stats.setCampeonatosVencidos(stats.getCampeonatosVencidos() + 1);
        } else if (campeaoSide != null) {
            stats.setViceCampeonatos(stats.getViceCampeonatos() + 1);
        }

        int wins = side == 1 ? agg.winsSide1 : agg.winsSide2;
        int losses = side == 1 ? agg.winsSide2 : agg.winsSide1;
        int goalsFor = side == 1 ? agg.goalsSide1 : agg.goalsSide2;
        int goalsAgainst = side == 1 ? agg.goalsSide2 : agg.goalsSide1;

        stats.setVitorias(stats.getVitorias() + wins);
        stats.setEmpates(stats.getEmpates() + agg.draws);
        stats.setDerrotas(stats.getDerrotas() + losses);
        stats.setGolsMarcados(stats.getGolsMarcados() + goalsFor);
        stats.setGolsSofridos(stats.getGolsSofridos() + goalsAgainst);
        stats.setSaldoGols(stats.getGolsMarcados() - stats.getGolsSofridos());
        stats.setDataUltimoCampeonato(finishedAt);

        atualizarStreaks(stats, side, agg.resultadosOrdenados);
        atualizarClubesUsados(stats, user, side, campeonato, campeaoGlobal, campeaoSide);

        estatisticaUsuarioRepository.save(stats);
    }

    private void atualizarStreaks(EstatisticaUsuario stats, int side, List<Integer> resultados) {
        int currentWin = stats.getSequenciaVitoriasAtual();
        int currentUnb = stats.getSequenciaInvictaAtual();
        int longestWin = stats.getMaiorSequenciaVitorias();
        int longestUnb = stats.getMaiorSequenciaInvicta();

        for (Integer resultado : resultados) {
            if (resultado == 0) {
                currentWin = 0;
                currentUnb++;
            } else if (resultado == side) {
                currentWin++;
                currentUnb++;
            } else {
                currentWin = 0;
                currentUnb = 0;
            }
            longestWin = Math.max(longestWin, currentWin);
            longestUnb = Math.max(longestUnb, currentUnb);
        }

        stats.setSequenciaVitoriasAtual(currentWin);
        stats.setSequenciaInvictaAtual(currentUnb);
        stats.setMaiorSequenciaVitorias(longestWin);
        stats.setMaiorSequenciaInvicta(longestUnb);
    }

    private void atualizarClubesUsados(EstatisticaUsuario stats, User user, int side, Campeonato campeonato,
            Clube campeaoGlobal, Integer campeaoSide) {
        if (campeonato.getClubes() == null) {
            return;
        }

        Set<Long> novosClubes = new HashSet<>();
        Long tituloClubeOrigemId = null;
        if (campeaoSide != null && campeaoSide == side && campeonato.getCampeaoClube() != null) {
            tituloClubeOrigemId = campeonato.getCampeaoClube().getClubeOrigemId();
        }

        Map<Long, Clube> globais = new HashMap<>();
        for (CampeonatoClube snapshot : campeonato.getClubes()) {
            if (!Integer.valueOf(side).equals(snapshot.getCompetidorNumero())) {
                continue;
            }
            Long origemId = snapshot.getClubeOrigemId();
            if (origemId == null) {
                continue;
            }
            Clube global = globais.computeIfAbsent(origemId, id -> clubeRepository.findById(id).orElse(null));
            if (global == null) {
                continue;
            }

            UsoClubeUsuario usage = usoClubeUsuarioRepository
                    .findByUsuarioUserIdAndClubeClubeId(user.getUserId(), global.getClubeId())
                    .orElse(null);
            boolean novo = usage == null;
            if (novo) {
                usage = new UsoClubeUsuario();
                usage.setUsuario(user);
                usage.setClube(global);
                novosClubes.add(global.getClubeId());
            }
            usage.setVezesUsado(usage.getVezesUsado() + 1);
            if (tituloClubeOrigemId != null && tituloClubeOrigemId.equals(origemId)) {
                usage.setTitulos(usage.getTitulos() + 1);
            }
            usoClubeUsuarioRepository.save(usage);
        }

        if (!novosClubes.isEmpty()) {
            stats.setClubesUsados(stats.getClubesUsados() + novosClubes.size());
        }

        List<UsoClubeUsuario> todos = usoClubeUsuarioRepository.findByUsuarioIdComClube(user.getUserId());
        UsoClubeUsuario favorito = null;
        UsoClubeUsuario maisTitulos = null;
        for (UsoClubeUsuario u : todos) {
            if (favorito == null || u.getVezesUsado() > favorito.getVezesUsado()) {
                favorito = u;
            }
            if (maisTitulos == null || u.getTitulos() > maisTitulos.getTitulos()) {
                maisTitulos = u;
            }
        }
        if (favorito != null) {
            stats.setClubeFavorito(favorito.getClube());
        }
        if (maisTitulos != null && maisTitulos.getTitulos() > 0) {
            stats.setClubeMaisTitulos(maisTitulos.getClube());
        } else if (campeaoGlobal != null && campeaoSide != null && campeaoSide == side) {
            stats.setClubeMaisTitulos(campeaoGlobal);
        }
    }

    private void atualizarRivalidade(User user1, User user2, User winnerUser, MatchAgg agg,
            LocalDateTime finishedAt, String nomeCampeonato) {
        User userA;
        User userB;
        int winsA;
        int winsB;
        int goalsA;
        int goalsB;
        boolean user1IsA = user1.getUserId() < user2.getUserId();

        if (user1IsA) {
            userA = user1;
            userB = user2;
            winsA = agg.winsSide1;
            winsB = agg.winsSide2;
            goalsA = agg.goalsSide1;
            goalsB = agg.goalsSide2;
        } else {
            userA = user2;
            userB = user1;
            winsA = agg.winsSide2;
            winsB = agg.winsSide1;
            goalsA = agg.goalsSide2;
            goalsB = agg.goalsSide1;
        }

        RivalidadeUsuario rivalry = rivalidadeUsuarioRepository
                .findByUsuarioAUserIdAndUsuarioBUserId(userA.getUserId(), userB.getUserId())
                .orElseGet(() -> {
                    RivalidadeUsuario r = new RivalidadeUsuario();
                    r.setUsuarioA(userA);
                    r.setUsuarioB(userB);
                    return r;
                });

        rivalry.setConfrontos(rivalry.getConfrontos() + agg.matchesPlayed);
        rivalry.setVitoriasUsuarioA(rivalry.getVitoriasUsuarioA() + winsA);
        rivalry.setVitoriasUsuarioB(rivalry.getVitoriasUsuarioB() + winsB);
        rivalry.setEmpates(rivalry.getEmpates() + agg.draws);
        rivalry.setGolsUsuarioA(rivalry.getGolsUsuarioA() + goalsA);
        rivalry.setGolsUsuarioB(rivalry.getGolsUsuarioB() + goalsB);
        rivalry.setCampeonatosDisputados(rivalry.getCampeonatosDisputados() + 1);
        rivalry.setDataUltimoConfronto(finishedAt);
        rivalry.setUltimoVencedor(winnerUser);

        if (winnerUser != null) {
            if (winnerUser.getUserId().equals(userA.getUserId())) {
                rivalry.setTitulosUsuarioA(rivalry.getTitulosUsuarioA() + 1);
            } else if (winnerUser.getUserId().equals(userB.getUserId())) {
                rivalry.setTitulosUsuarioB(rivalry.getTitulosUsuarioB() + 1);
            }
        }

        atualizarMaiorGoleadaRivalidade(rivalry, user1IsA, agg, nomeCampeonato);
        atualizarSequenciasRivalidade(rivalry, userA, userB, user1IsA, agg.resultadosOrdenados);

        rivalidadeUsuarioRepository.save(rivalry);
    }

    private void atualizarMaiorGoleadaRivalidade(RivalidadeUsuario rivalry, boolean user1IsA,
            MatchAgg agg, String nomeCampeonato) {
        GoleadaCand goleadaA = user1IsA ? agg.goleadaSide1 : agg.goleadaSide2;
        GoleadaCand goleadaB = user1IsA ? agg.goleadaSide2 : agg.goleadaSide1;

        if (goleadaA.margem > rivalry.getMaiorGoleadaAMargem()
                || (goleadaA.margem == rivalry.getMaiorGoleadaAMargem()
                        && goleadaA.golsPro > rivalry.getMaiorGoleadaAGolsPro())) {
            aplicarGoleadaA(rivalry, goleadaA, nomeCampeonato);
        }
        if (goleadaB.margem > rivalry.getMaiorGoleadaBMargem()
                || (goleadaB.margem == rivalry.getMaiorGoleadaBMargem()
                        && goleadaB.golsPro > rivalry.getMaiorGoleadaBGolsPro())) {
            aplicarGoleadaB(rivalry, goleadaB, nomeCampeonato);
        }
    }

    private static void aplicarGoleadaA(RivalidadeUsuario rivalry, GoleadaCand cand, String nomeCampeonato) {
        if (cand.margem <= 0) {
            return;
        }
        rivalry.setMaiorGoleadaAMargem(cand.margem);
        rivalry.setMaiorGoleadaAGolsPro(cand.golsPro);
        rivalry.setMaiorGoleadaAGolsContra(cand.golsContra);
        rivalry.setMaiorGoleadaAClubePro(cand.clubePro);
        rivalry.setMaiorGoleadaAClubeContra(cand.clubeContra);
        rivalry.setMaiorGoleadaACampeonatoNome(nomeCampeonato);
    }

    private static void aplicarGoleadaB(RivalidadeUsuario rivalry, GoleadaCand cand, String nomeCampeonato) {
        if (cand.margem <= 0) {
            return;
        }
        rivalry.setMaiorGoleadaBMargem(cand.margem);
        rivalry.setMaiorGoleadaBGolsPro(cand.golsPro);
        rivalry.setMaiorGoleadaBGolsContra(cand.golsContra);
        rivalry.setMaiorGoleadaBClubePro(cand.clubePro);
        rivalry.setMaiorGoleadaBClubeContra(cand.clubeContra);
        rivalry.setMaiorGoleadaBCampeonatoNome(nomeCampeonato);
    }

    private void atualizarSequenciasRivalidade(RivalidadeUsuario rivalry, User userA, User userB,
            boolean user1IsA, List<Integer> resultadosSide) {
        int seqAtual = rivalry.getSequenciaAtual();
        Long seqUserId = rivalry.getSequenciaAtualUsuario() != null
                ? rivalry.getSequenciaAtualUsuario().getUserId()
                : null;
        int maiorA = rivalry.getMaiorSequenciaVitoriasA();
        int maiorB = rivalry.getMaiorSequenciaVitoriasB();

        for (Integer resultadoSide : resultadosSide) {
            int resultadoCanonico;
            if (resultadoSide == 0) {
                resultadoCanonico = 0;
            } else if (user1IsA) {
                resultadoCanonico = resultadoSide;
            } else {
                resultadoCanonico = resultadoSide == 1 ? 2 : 1;
            }

            if (resultadoCanonico == 0) {
                seqAtual = 0;
                seqUserId = null;
            } else if (resultadoCanonico == 1) {
                if (seqUserId != null && seqUserId.equals(userA.getUserId())) {
                    seqAtual++;
                } else {
                    seqUserId = userA.getUserId();
                    seqAtual = 1;
                }
                maiorA = Math.max(maiorA, seqAtual);
            } else {
                if (seqUserId != null && seqUserId.equals(userB.getUserId())) {
                    seqAtual++;
                } else {
                    seqUserId = userB.getUserId();
                    seqAtual = 1;
                }
                maiorB = Math.max(maiorB, seqAtual);
            }
        }

        rivalry.setMaiorSequenciaVitoriasA(maiorA);
        rivalry.setMaiorSequenciaVitoriasB(maiorB);
        rivalry.setSequenciaAtual(seqAtual);
        if (seqUserId == null) {
            rivalry.setSequenciaAtualUsuario(null);
        } else if (seqUserId.equals(userA.getUserId())) {
            rivalry.setSequenciaAtualUsuario(userA);
        } else {
            rivalry.setSequenciaAtualUsuario(userB);
        }
    }

    private static final class GoleadaCand {
        int margem;
        int golsPro;
        int golsContra;
        String clubePro;
        String clubeContra;
    }

    private static final class MatchAgg {
        int matchesPlayed;
        int goalsSide1;
        int goalsSide2;
        int winsSide1;
        int winsSide2;
        int draws;
        final GoleadaCand goleadaSide1 = new GoleadaCand();
        final GoleadaCand goleadaSide2 = new GoleadaCand();
        /** 0 = empate regulamentar, 1 = vitória lado 1, 2 = vitória lado 2 */
        final List<Integer> resultadosOrdenados = new ArrayList<>();
    }
}
