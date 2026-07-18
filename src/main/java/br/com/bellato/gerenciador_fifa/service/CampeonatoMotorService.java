package br.com.bellato.gerenciador_fifa.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.campeonato.DefinirVencedorRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RemanejamentoConfrontoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RemanejamentoInfoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RemanejamentoRequestDTO;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.enums.StatusPartida;
import br.com.bellato.gerenciador_fifa.enums.StatusRodada;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.mapper.campeonato.CampeonatoMapper;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;
import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoPartidaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoRepository;

@Service
public class CampeonatoMotorService {

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private CampeonatoPartidaRepository campeonatoPartidaRepository;

    @Transactional
    public Campeonato iniciarCampeonato(Campeonato campeonato) {
        if (campeonato.getStatus() == StatusCampeonato.FINALIZADO
                || campeonato.getStatus() == StatusCampeonato.AGUARDANDO_FINALIZACAO) {
            throw new CampeonatoBusinessException("Campeonato já possui campeão definido ou está finalizado.");
        }
        if (campeonato.getRodadas() != null && !campeonato.getRodadas().isEmpty()) {
            throw new CampeonatoBusinessException("O campeonato já foi iniciado.");
        }

        campeonato.setStatus(StatusCampeonato.PRONTO_PARA_SORTEIO);
        distribuirClubesEntreCompetidores(campeonato);

        campeonato.setStatus(StatusCampeonato.PRIMEIRA_RODADA);
        List<CampeonatoClube> classificados = obterClassificados(campeonato);
        gerarProximaRodadaAutomatica(campeonato, classificados);

        return campeonatoRepository.save(campeonato);
    }

    @Transactional
    public Campeonato iniciarPorId(Long campeonatoId) {
        Campeonato campeonato = obterCampeonato(campeonatoId);
        return iniciarCampeonato(campeonato);
    }

    @Transactional
    public Campeonato definirVencedor(Long campeonatoId, Long partidaId, DefinirVencedorRequestDTO request) {
        if (request == null || request.getCampeonatoClubeId() == null) {
            throw new CampeonatoBusinessException("Informe o clube vencedor da partida.");
        }

        Campeonato campeonato = obterCampeonato(campeonatoId);
        validarCampeonatoPermiteResultados(campeonato);

        CampeonatoPartida partida = campeonatoPartidaRepository
                .findByCampeonatoPartidaIdAndCampeonatoRodadaCampeonatoCampeonatoId(partidaId, campeonatoId)
                .orElseThrow(() -> new CampeonatoBusinessException("Partida não encontrada neste campeonato."));

        if (partida.getStatus() == StatusPartida.FINALIZADA) {
            throw new CampeonatoBusinessException("Esta partida já possui vencedor definido.");
        }

        CampeonatoClube vencedor = resolverClubeDaPartida(partida, request.getCampeonatoClubeId());
        CampeonatoClube perdedor = partida.getClubeMandante().getCampeonatoClubeId().equals(vencedor.getCampeonatoClubeId())
                ? partida.getClubeVisitante()
                : partida.getClubeMandante();

        partida.setClubeVencedor(vencedor);
        partida.setStatus(StatusPartida.FINALIZADA);
        perdedor.setEliminado(true);

        processarAposResultado(campeonato);
        return campeonatoRepository.save(campeonato);
    }

    @Transactional
    public Campeonato escolherCampeao(Long campeonatoId, DefinirVencedorRequestDTO request) {
        if (request == null || request.getCampeonatoClubeId() == null) {
            throw new CampeonatoBusinessException("Selecione o clube campeão.");
        }

        Campeonato campeonato = obterCampeonato(campeonatoId);
        if (campeonato.getStatus() != StatusCampeonato.AGUARDANDO_ESCOLHA_DO_CAMPEAO) {
            throw new CampeonatoBusinessException("O campeonato não está aguardando escolha do clube campeão.");
        }
        if (campeonato.getCampeaoClube() != null) {
            throw new CampeonatoBusinessException("O clube campeão já foi definido para este campeonato.");
        }

        CampeonatoClube escolhido = campeonato.getClubes().stream()
                .filter(c -> Objects.equals(c.getCampeonatoClubeId(), request.getCampeonatoClubeId()))
                .findFirst()
                .orElseThrow(() -> new CampeonatoBusinessException("Clube informado não pertence a este campeonato."));

        if (!escolhido.isClassificado()) {
            throw new CampeonatoBusinessException("Somente clubes sobreviventes podem ser escolhidos como campeão.");
        }

        Integer competidorVencedor = campeonato.getCampeaoCompetidor();
        if (competidorVencedor == null) {
            throw new CampeonatoBusinessException("Competidor campeão ainda não foi definido.");
        }
        if (!Objects.equals(escolhido.getCompetidorNumero(), competidorVencedor)) {
            throw new CampeonatoBusinessException("O clube escolhido não pertence ao competidor campeão.");
        }

        registrarCampeao(campeonato, escolhido);
        return campeonatoRepository.save(campeonato);
    }

    @Transactional(readOnly = true)
    public RemanejamentoInfoResponseDTO obterInfoRemanejamento(Long campeonatoId) {
        Campeonato campeonato = obterCampeonato(campeonatoId);
        RemanejamentoInfoResponseDTO info = new RemanejamentoInfoResponseDTO();

        if (campeonato.getStatus() != StatusCampeonato.AGUARDANDO_REMANEJAMENTO) {
            info.setNecessario(false);
            return info;
        }

        List<CampeonatoClube> classificados1 = obterClassificadosPorCompetidor(campeonato, 1);
        List<CampeonatoClube> classificados2 = obterClassificadosPorCompetidor(campeonato, 2);

        int qtd1 = classificados1.size();
        int qtd2 = classificados2.size();

        if (qtd1 == qtd2 || qtd1 == 0 || qtd2 == 0) {
            info.setNecessario(false);
            return info;
        }

        boolean vantagem1 = qtd1 > qtd2;
        List<CampeonatoClube> vantagem = vantagem1 ? classificados1 : classificados2;
        List<CampeonatoClube> minoria = vantagem1 ? classificados2 : classificados1;
        int competidorVantagem = vantagem1 ? 1 : 2;
        int competidorMinoria = vantagem1 ? 2 : 1;

        info.setNecessario(true);
        info.setCompetidorVantagem(competidorVantagem);
        info.setCompetidorVantagemNome(nomeCompetidor(campeonato, competidorVantagem));
        info.setCompetidorMinoria(competidorMinoria);
        info.setCompetidorMinoriaNome(nomeCompetidor(campeonato, competidorMinoria));
        info.setQuantidadeConfrontos(minoria.size());
        info.setClubesVantagem(vantagem.stream().map(CampeonatoMapper::toClubeDTO).collect(Collectors.toList()));
        info.setClubesMinoria(minoria.stream().map(CampeonatoMapper::toClubeDTO).collect(Collectors.toList()));
        info.setClubesBye(Collections.emptyList());
        return info;
    }

    @Transactional
    public Campeonato confirmarRemanejamento(Long campeonatoId, RemanejamentoRequestDTO request) {
        Campeonato campeonato = obterCampeonato(campeonatoId);
        if (campeonato.getStatus() != StatusCampeonato.AGUARDANDO_REMANEJAMENTO) {
            throw new CampeonatoBusinessException("O campeonato não está aguardando remanejamento.");
        }
        if (request == null || request.getConfrontos() == null || request.getConfrontos().isEmpty()) {
            throw new CampeonatoBusinessException("Informe os confrontos do remanejamento.");
        }

        List<CampeonatoClube> classificados1 = obterClassificadosPorCompetidor(campeonato, 1);
        List<CampeonatoClube> classificados2 = obterClassificadosPorCompetidor(campeonato, 2);
        boolean vantagem1 = classificados1.size() > classificados2.size();
        List<CampeonatoClube> vantagem = vantagem1 ? classificados1 : classificados2;
        List<CampeonatoClube> minoria = vantagem1 ? classificados2 : classificados1;
        int competidorVantagem = vantagem1 ? 1 : 2;

        validarRemanejamento(request.getConfrontos(), vantagem, minoria);

        Map<Long, CampeonatoClube> porId = campeonato.getClubes().stream()
                .collect(Collectors.toMap(CampeonatoClube::getCampeonatoClubeId, Function.identity()));

        List<CampeonatoClube[]> pares = new ArrayList<>();
        Set<Long> usadosVantagem = new HashSet<>();

        for (RemanejamentoConfrontoDTO confronto : request.getConfrontos()) {
            CampeonatoClube clubeVantagem = porId.get(confronto.getClubeCompetidorVantagemId());
            CampeonatoClube clubeMinoria = porId.get(confronto.getClubeCompetidorMinoriaId());
            usadosVantagem.add(clubeVantagem.getCampeonatoClubeId());

            if (competidorVantagem == 1) {
                pares.add(new CampeonatoClube[] { clubeVantagem, clubeMinoria });
            } else {
                pares.add(new CampeonatoClube[] { clubeMinoria, clubeVantagem });
            }
        }

        campeonato.setStatus(StatusCampeonato.GERANDO_PROXIMA_RODADA);
        criarRodadaComPares(campeonato, pares, true, competidorVantagem);
        campeonato.setStatus(StatusCampeonato.RODADA_EM_ANDAMENTO);

        return campeonatoRepository.save(campeonato);
    }

    private void validarCampeonatoPermiteResultados(Campeonato campeonato) {
        StatusCampeonato status = campeonato.getStatus();
        if (status == StatusCampeonato.FINALIZADO || status == StatusCampeonato.AGUARDANDO_FINALIZACAO) {
            throw new CampeonatoBusinessException("Campeonato já possui campeão definido ou está finalizado.");
        }
        if (status == StatusCampeonato.AGUARDANDO_ESCOLHA_DO_CAMPEAO) {
            throw new CampeonatoBusinessException("Selecione o clube campeão antes de continuar.");
        }
        if (status == StatusCampeonato.AGUARDANDO_REMANEJAMENTO) {
            throw new CampeonatoBusinessException("Conclua o remanejamento antes de definir resultados.");
        }
    }

    private void distribuirClubesEntreCompetidores(Campeonato campeonato) {
        List<CampeonatoClube> paraSorteio = new ArrayList<>();
        CampeonatoClube campeaoProtegido = null;

        for (CampeonatoClube clube : campeonato.getClubes()) {
            clube.setEliminado(false);
            if (Boolean.TRUE.equals(clube.getExcluidoSorteio()) || Boolean.TRUE.equals(clube.getCampeaoAnterior())) {
                if (clube.getCompetidorNumero() == null) {
                    throw new CampeonatoBusinessException(
                            "Clube campeão anterior sem competidor vinculado: " + clube.getNome());
                }
                campeaoProtegido = clube;
            } else {
                paraSorteio.add(clube);
            }
        }

        Collections.shuffle(paraSorteio);

        int total = campeonato.getClubes().size();
        if (total % 2 != 0) {
            throw new CampeonatoBusinessException("A quantidade de clubes do campeonato deve ser par.");
        }

        int porCompetidor = total / 2;
        int slotsCompetidor1 = porCompetidor;
        int slotsCompetidor2 = porCompetidor;

        if (campeaoProtegido != null) {
            if (campeaoProtegido.getCompetidorNumero() == 1) {
                slotsCompetidor1--;
            } else {
                slotsCompetidor2--;
            }
        }

        if (paraSorteio.size() != slotsCompetidor1 + slotsCompetidor2) {
            throw new CampeonatoBusinessException("Inconsistência no sorteio de distribuição dos clubes.");
        }

        int indice = 0;
        for (int i = 0; i < slotsCompetidor1; i++) {
            paraSorteio.get(indice++).setCompetidorNumero(1);
        }
        for (int i = 0; i < slotsCompetidor2; i++) {
            paraSorteio.get(indice++).setCompetidorNumero(2);
        }

        validarIntegridadeDistribuicao(campeonato, porCompetidor);
    }

    private void validarIntegridadeDistribuicao(Campeonato campeonato, int porCompetidor) {
        long c1 = campeonato.getClubes().stream().filter(c -> Objects.equals(c.getCompetidorNumero(), 1)).count();
        long c2 = campeonato.getClubes().stream().filter(c -> Objects.equals(c.getCompetidorNumero(), 2)).count();
        long semCompetidor = campeonato.getClubes().stream().filter(c -> c.getCompetidorNumero() == null).count();

        if (semCompetidor > 0) {
            throw new CampeonatoBusinessException("Existem clubes sem competidor após o sorteio.");
        }
        if (c1 != porCompetidor || c2 != porCompetidor) {
            throw new CampeonatoBusinessException(
                    "Distribuição inválida: Competidor 1=" + c1 + ", Competidor 2=" + c2);
        }

        long idsUnicos = campeonato.getClubes().stream()
                .map(CampeonatoClube::getCampeonatoClubeId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        if (idsUnicos != campeonato.getClubes().size()
                && campeonato.getClubes().stream().anyMatch(c -> c.getCampeonatoClubeId() != null)) {
            throw new CampeonatoBusinessException("Detectada duplicidade de clubes no campeonato.");
        }
    }

    private void gerarProximaRodadaAutomatica(Campeonato campeonato, List<CampeonatoClube> classificados) {
        List<CampeonatoClube> c1 = classificados.stream()
                .filter(c -> Objects.equals(c.getCompetidorNumero(), 1))
                .collect(Collectors.toCollection(ArrayList::new));
        List<CampeonatoClube> c2 = classificados.stream()
                .filter(c -> Objects.equals(c.getCompetidorNumero(), 2))
                .collect(Collectors.toCollection(ArrayList::new));

        if (c1.isEmpty() || c2.isEmpty()) {
            throw new CampeonatoBusinessException(
                    "Não é possível gerar rodada: um dos competidores não possui clubes classificados.");
        }

        if (c1.size() != c2.size()) {
            campeonato.setStatus(StatusCampeonato.AGUARDANDO_REMANEJAMENTO);
            return;
        }

        boolean primeiraRodada = campeonato.getRodadaAtual() == null || campeonato.getRodadaAtual() == 0;
        List<CampeonatoClube[]> pares;

        if (primeiraRodada) {
            pares = montarParesPrimeiraRodada(c1, c2);
        } else {
            Collections.shuffle(c1);
            Collections.shuffle(c2);
            pares = new ArrayList<>();
            for (int i = 0; i < c1.size(); i++) {
                pares.add(new CampeonatoClube[] { c1.get(i), c2.get(i) });
            }
        }

        campeonato.setStatus(StatusCampeonato.GERANDO_PROXIMA_RODADA);
        criarRodadaComPares(campeonato, pares, false, null);
        campeonato.setStatus(StatusCampeonato.RODADA_EM_ANDAMENTO);
    }

    /**
     * Na primeira rodada, o clube campeão protegido abre o campeonato no confronto 1.
     * O adversário é sorteado aleatoriamente; demais confrontos seguem o sorteio normal.
     */
    private List<CampeonatoClube[]> montarParesPrimeiraRodada(List<CampeonatoClube> c1, List<CampeonatoClube> c2) {
        List<CampeonatoClube> competidor1 = new ArrayList<>(c1);
        List<CampeonatoClube> competidor2 = new ArrayList<>(c2);

        CampeonatoClube campeaoEncontrado = null;
        for (CampeonatoClube clube : competidor1) {
            if (Boolean.TRUE.equals(clube.getCampeaoAnterior()) || Boolean.TRUE.equals(clube.getExcluidoSorteio())) {
                campeaoEncontrado = clube;
                break;
            }
        }
        if (campeaoEncontrado == null) {
            for (CampeonatoClube clube : competidor2) {
                if (Boolean.TRUE.equals(clube.getCampeaoAnterior()) || Boolean.TRUE.equals(clube.getExcluidoSorteio())) {
                    campeaoEncontrado = clube;
                    break;
                }
            }
        }

        final CampeonatoClube campeaoProtegido = campeaoEncontrado;

        Collections.shuffle(competidor1);
        Collections.shuffle(competidor2);

        List<CampeonatoClube[]> pares = new ArrayList<>();

        if (campeaoProtegido == null) {
            for (int i = 0; i < competidor1.size(); i++) {
                pares.add(new CampeonatoClube[] { competidor1.get(i), competidor2.get(i) });
            }
            return pares;
        }

        competidor1.removeIf(c -> Objects.equals(c.getCampeonatoClubeId(), campeaoProtegido.getCampeonatoClubeId())
                || (c.getCampeonatoClubeId() == null && c == campeaoProtegido));
        competidor2.removeIf(c -> Objects.equals(c.getCampeonatoClubeId(), campeaoProtegido.getCampeonatoClubeId())
                || (c.getCampeonatoClubeId() == null && c == campeaoProtegido));

        if (Objects.equals(campeaoProtegido.getCompetidorNumero(), 1)) {
            if (competidor2.isEmpty()) {
                throw new CampeonatoBusinessException("Não há adversário disponível para o clube campeão protegido.");
            }
            CampeonatoClube adversario = competidor2.remove(0);
            pares.add(new CampeonatoClube[] { campeaoProtegido, adversario });
        } else if (Objects.equals(campeaoProtegido.getCompetidorNumero(), 2)) {
            if (competidor1.isEmpty()) {
                throw new CampeonatoBusinessException("Não há adversário disponível para o clube campeão protegido.");
            }
            CampeonatoClube adversario = competidor1.remove(0);
            pares.add(new CampeonatoClube[] { adversario, campeaoProtegido });
        } else {
            throw new CampeonatoBusinessException("Clube campeão protegido sem competidor vinculado.");
        }

        if (competidor1.size() != competidor2.size()) {
            throw new CampeonatoBusinessException(
                    "Inconsistência ao montar confrontos após reservar o clube campeão protegido.");
        }

        for (int i = 0; i < competidor1.size(); i++) {
            pares.add(new CampeonatoClube[] { competidor1.get(i), competidor2.get(i) });
        }

        return pares;
    }

    private void criarRodadaComPares(Campeonato campeonato, List<CampeonatoClube[]> pares,
            boolean faseAssincrona, Integer competidorRemanejamento) {
        validarPares(pares);

        int numero = (campeonato.getRodadaAtual() == null ? 0 : campeonato.getRodadaAtual()) + 1;
        CampeonatoRodada rodada = new CampeonatoRodada();
        rodada.setCampeonato(campeonato);
        rodada.setNumeroRodada(numero);
        rodada.setNome(nomeRodada(campeonato, pares.size(), faseAssincrona));
        rodada.setStatus(StatusRodada.EM_ANDAMENTO);
        rodada.setFaseAssincrona(faseAssincrona);
        rodada.setCompetidorRemanejamento(competidorRemanejamento);

        int ordem = 1;
        for (CampeonatoClube[] par : pares) {
            CampeonatoPartida partida = new CampeonatoPartida();
            partida.setCampeonatoRodada(rodada);
            partida.setClubeMandante(par[0]);
            partida.setClubeVisitante(par[1]);
            partida.setStatus(StatusPartida.AGUARDANDO_RESULTADO);
            partida.setOrdem(ordem++);
            rodada.getPartidas().add(partida);
        }

        campeonato.getRodadas().add(rodada);
        campeonato.setRodadaAtual(numero);
    }

    private void validarPares(List<CampeonatoClube[]> pares) {
        if (pares == null || pares.isEmpty()) {
            throw new CampeonatoBusinessException("Nenhum confronto para gerar a rodada.");
        }
        Set<Long> usados = new HashSet<>();
        for (CampeonatoClube[] par : pares) {
            if (par[0] == null || par[1] == null) {
                throw new CampeonatoBusinessException("Confronto inválido: clube nulo.");
            }
            if (Objects.equals(par[0].getCampeonatoClubeId(), par[1].getCampeonatoClubeId())
                    && par[0].getCampeonatoClubeId() != null) {
                throw new CampeonatoBusinessException("Um clube não pode enfrentar a si mesmo.");
            }
            if (Objects.equals(par[0].getCompetidorNumero(), par[1].getCompetidorNumero())) {
                throw new CampeonatoBusinessException(
                        "Confrontos devem ser entre clubes de competidores diferentes.");
            }
            if (par[0].getCampeonatoClubeId() != null && !usados.add(par[0].getCampeonatoClubeId())) {
                throw new CampeonatoBusinessException("Clube duplicado nos confrontos: " + par[0].getNome());
            }
            if (par[1].getCampeonatoClubeId() != null && !usados.add(par[1].getCampeonatoClubeId())) {
                throw new CampeonatoBusinessException("Clube duplicado nos confrontos: " + par[1].getNome());
            }
        }
    }

    private void processarAposResultado(Campeonato campeonato) {
        CampeonatoRodada rodadaAtual = obterRodadaAtual(campeonato);
        if (rodadaAtual == null) {
            return;
        }

        List<CampeonatoClube> classificados = obterClassificados(campeonato);
        List<CampeonatoClube> c1 = classificados.stream()
                .filter(c -> Objects.equals(c.getCompetidorNumero(), 1))
                .collect(Collectors.toList());
        List<CampeonatoClube> c2 = classificados.stream()
                .filter(c -> Objects.equals(c.getCompetidorNumero(), 2))
                .collect(Collectors.toList());

        // Eliminação total: verifica imediatamente após cada resultado
        if (c1.isEmpty() || c2.isEmpty()) {
            boolean todasFinalizadas = rodadaAtual.getPartidas().stream()
                    .allMatch(p -> p.getStatus() == StatusPartida.FINALIZADA);
            if (todasFinalizadas) {
                rodadaAtual.setStatus(StatusRodada.FINALIZADA);
            }
            tratarEliminacaoTotal(campeonato, c1, c2);
            return;
        }

        boolean todasFinalizadas = rodadaAtual.getPartidas().stream()
                .allMatch(p -> p.getStatus() == StatusPartida.FINALIZADA);

        if (!todasFinalizadas) {
            campeonato.setStatus(StatusCampeonato.RODADA_EM_ANDAMENTO);
            return;
        }

        rodadaAtual.setStatus(StatusRodada.FINALIZADA);
        campeonato.setStatus(StatusCampeonato.RODADA_FINALIZADA);

        gerarProximaRodadaAutomatica(campeonato, classificados);
    }

    /**
     * Competidor campeão é definido automaticamente.
     * Clube campeão: se houver apenas 1 sobrevivente, registra automaticamente;
     * se houver mais de um, aguarda escolha manual entre todos os vivos do competidor.
     */
    private void tratarEliminacaoTotal(Campeonato campeonato,
            List<CampeonatoClube> c1, List<CampeonatoClube> c2) {
        if (c1.isEmpty() && c2.isEmpty()) {
            throw new CampeonatoBusinessException("Estado inválido: nenhum clube classificado.");
        }

        List<CampeonatoClube> sobreviventes = c1.isEmpty() ? c2 : c1;
        Integer competidorVencedor = sobreviventes.get(0).getCompetidorNumero();

        campeonato.setCampeaoCompetidor(competidorVencedor);

        if (sobreviventes.size() == 1) {
            registrarCampeao(campeonato, sobreviventes.get(0));
            return;
        }

        campeonato.setCampeaoClube(null);
        campeonato.setStatus(StatusCampeonato.AGUARDANDO_ESCOLHA_DO_CAMPEAO);
    }

    private void registrarCampeao(Campeonato campeonato, CampeonatoClube campeao) {
        campeonato.setCampeaoClube(campeao);
        campeonato.setCampeaoCompetidor(campeao.getCompetidorNumero());
        campeonato.setStatus(StatusCampeonato.AGUARDANDO_FINALIZACAO);
    }

    private void validarRemanejamento(List<RemanejamentoConfrontoDTO> confrontos,
            List<CampeonatoClube> vantagem, List<CampeonatoClube> minoria) {
        if (confrontos.size() != minoria.size()) {
            throw new CampeonatoBusinessException(
                    "Devem existir exatamente " + minoria.size() + " confrontos no remanejamento.");
        }

        Set<Long> idsVantagem = vantagem.stream()
                .map(CampeonatoClube::getCampeonatoClubeId)
                .collect(Collectors.toSet());
        Set<Long> idsMinoria = minoria.stream()
                .map(CampeonatoClube::getCampeonatoClubeId)
                .collect(Collectors.toSet());

        Set<Long> usadosVantagem = new HashSet<>();
        Set<Long> usadosMinoria = new HashSet<>();

        for (RemanejamentoConfrontoDTO confronto : confrontos) {
            if (confronto.getClubeCompetidorVantagemId() == null || confronto.getClubeCompetidorMinoriaId() == null) {
                throw new CampeonatoBusinessException("Cada confronto deve informar os dois clubes.");
            }
            if (!idsVantagem.contains(confronto.getClubeCompetidorVantagemId())) {
                throw new CampeonatoBusinessException(
                        "Clube informado não pertence ao competidor com vantagem.");
            }
            if (!idsMinoria.contains(confronto.getClubeCompetidorMinoriaId())) {
                throw new CampeonatoBusinessException(
                        "Clube informado não pertence ao competidor em minoria.");
            }
            if (!usadosVantagem.add(confronto.getClubeCompetidorVantagemId())) {
                throw new CampeonatoBusinessException("Clube duplicado no remanejamento (vantagem).");
            }
            if (!usadosMinoria.add(confronto.getClubeCompetidorMinoriaId())) {
                throw new CampeonatoBusinessException("Clube duplicado no remanejamento (minoria).");
            }
        }

        if (usadosMinoria.size() != minoria.size()) {
            throw new CampeonatoBusinessException("Todos os clubes da minoria devem participar dos confrontos.");
        }
    }

    private CampeonatoClube resolverClubeDaPartida(CampeonatoPartida partida, Long clubeId) {
        if (partida.getClubeMandante() != null
                && Objects.equals(partida.getClubeMandante().getCampeonatoClubeId(), clubeId)) {
            return partida.getClubeMandante();
        }
        if (partida.getClubeVisitante() != null
                && Objects.equals(partida.getClubeVisitante().getCampeonatoClubeId(), clubeId)) {
            return partida.getClubeVisitante();
        }
        throw new CampeonatoBusinessException("O clube informado não participa desta partida.");
    }

    private CampeonatoRodada obterRodadaAtual(Campeonato campeonato) {
        if (campeonato.getRodadas() == null || campeonato.getRodadas().isEmpty()) {
            return null;
        }
        Integer atual = campeonato.getRodadaAtual();
        return campeonato.getRodadas().stream()
                .filter(r -> Objects.equals(r.getNumeroRodada(), atual))
                .findFirst()
                .orElseGet(() -> campeonato.getRodadas().stream()
                        .max((a, b) -> Integer.compare(
                                a.getNumeroRodada() == null ? 0 : a.getNumeroRodada(),
                                b.getNumeroRodada() == null ? 0 : b.getNumeroRodada()))
                        .orElse(null));
    }

    private List<CampeonatoClube> obterClassificados(Campeonato campeonato) {
        return campeonato.getClubes().stream()
                .filter(CampeonatoClube::isClassificado)
                .collect(Collectors.toList());
    }

    private List<CampeonatoClube> obterClassificadosPorCompetidor(Campeonato campeonato, int competidor) {
        return obterClassificados(campeonato).stream()
                .filter(c -> Objects.equals(c.getCompetidorNumero(), competidor))
                .collect(Collectors.toList());
    }

    private String nomeRodada(Campeonato campeonato, int confrontos, boolean faseAssincrona) {
        if (faseAssincrona) {
            long rodadasAssincronasExistentes = campeonato.getRodadas() == null ? 0
                    : campeonato.getRodadas().stream()
                            .filter(r -> Boolean.TRUE.equals(r.getFaseAssincrona()))
                            .count();
            return "Rodada Assíncrona " + (rodadasAssincronasExistentes + 1);
        }

        // "Final" somente quando há exatamente um confronto síncrono (1 clube x 1 clube)
        if (confrontos == 1) {
            return "Final";
        }
        if (confrontos == 2) {
            return "Semifinal";
        }
        if (confrontos == 4) {
            return "Quartas de Final";
        }
        if (confrontos == 8) {
            return "Oitavas de Final";
        }
        return "Rodada " + ((campeonato.getRodadaAtual() == null ? 0 : campeonato.getRodadaAtual()) + 1);
    }

    private String nomeCompetidor(Campeonato campeonato, int numero) {
        return numero == 1 ? campeonato.getCompetidor1Nome() : campeonato.getCompetidor2Nome();
    }

    private Campeonato obterCampeonato(Long id) {
        return campeonatoRepository.findById(id)
                .orElseThrow(() -> new CampeonatoBusinessException("Campeonato não encontrado com o ID: " + id));
    }
}
