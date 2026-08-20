package br.com.bellato.gerenciador_fifa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaCompletoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaJogadorRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaSalvarRequestDTO;
import br.com.bellato.gerenciador_fifa.enums.FormacaoTatica;
import br.com.bellato.gerenciador_fifa.enums.TipoTaticaJogador;
import br.com.bellato.gerenciador_fifa.mapper.tatica.TaticaMapper;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.TaticaJogador;
import br.com.bellato.gerenciador_fifa.model.TaticaUsuarioClube;
import br.com.bellato.gerenciador_fifa.model.User;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.TaticaUsuarioClubeRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TaticaService {

    private static final int MAX_TITULARES = 11;

    @Autowired
    private TaticaUsuarioClubeRepository taticaRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private ClubeService clubeService;

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public TaticaCompletoResponseDTO obterPorClube(Long clubeId) {
        User usuario = userService.obterEntidadeAtual();
        Clube clube = clubeService.obterPorId(clubeId);
        List<Atleta> elenco = atletaRepository.findByClubeClubeId(clubeId);

        return taticaRepository.findByUsuarioAndClube(usuario.getUserId(), clubeId)
                .map(tatica -> TaticaMapper.toCompletoDTO(tatica, elenco))
                .orElseGet(() -> TaticaMapper.toVazioDTO(clube, elenco));
    }

    @Transactional
    public TaticaCompletoResponseDTO salvar(Long clubeId, TaticaSalvarRequestDTO request) {
        User usuario = userService.obterEntidadeAtual();
        Clube clube = clubeService.obterPorId(clubeId);
        List<Atleta> elenco = atletaRepository.findByClubeClubeId(clubeId);

        validarRequest(request, elenco);

        TaticaUsuarioClube tatica = taticaRepository
                .findByUsuarioAndClube(usuario.getUserId(), clubeId)
                .orElseGet(() -> criarNovaTatica(usuario, clube));

        aplicarCamposPrincipais(tatica, request);
        aplicarJogadores(tatica, request.getJogadores(), elenco);
        tatica.setDataUltimaAtualizacao(LocalDateTime.now());

        TaticaUsuarioClube salva = taticaRepository.save(tatica);
        return TaticaMapper.toCompletoDTO(salva, elenco);
    }

    @Transactional(readOnly = true)
    public TaticaCompletoResponseDTO obterPorId(Long taticaId) {
        User usuario = userService.obterEntidadeAtual();
        TaticaUsuarioClube tatica = taticaRepository.findByIdComDetalhes(taticaId)
                .orElseThrow(() -> new EntityNotFoundException("Tática não encontrada com o ID: " + taticaId));

        if (!tatica.getUsuario().getUserId().equals(usuario.getUserId())) {
            throw new IllegalArgumentException("Você não tem permissão para acessar esta tática.");
        }

        Long clubeId = tatica.getClube().getClubeId();
        List<Atleta> elenco = atletaRepository.findByClubeClubeId(clubeId);
        return TaticaMapper.toCompletoDTO(tatica, elenco);
    }

    private TaticaUsuarioClube criarNovaTatica(User usuario, Clube clube) {
        TaticaUsuarioClube tatica = new TaticaUsuarioClube();
        tatica.setUsuario(usuario);
        tatica.setClube(clube);
        tatica.setFormacao(FormacaoTatica.F_4_4_2);
        tatica.setDataUltimaAtualizacao(LocalDateTime.now());
        return tatica;
    }

    private void aplicarCamposPrincipais(TaticaUsuarioClube tatica, TaticaSalvarRequestDTO request) {
        if (request.getFormacao() != null) {
            tatica.setFormacao(request.getFormacao());
        }
        tatica.setAnotacoes(request.getAnotacoes());
        tatica.setCapitaoAtletaId(request.getCapitaoAtletaId());
        tatica.setBatedorPenaltisAtletaId(request.getBatedorPenaltisAtletaId());
        tatica.setBatedorFaltaAtletaId(request.getBatedorFaltaAtletaId());
        tatica.setBatedorEscanteioEsquerdoAtletaId(request.getBatedorEscanteioEsquerdoAtletaId());
        tatica.setBatedorEscanteioDireitoAtletaId(request.getBatedorEscanteioDireitoAtletaId());
    }

    private void aplicarJogadores(TaticaUsuarioClube tatica, List<TaticaJogadorRequestDTO> jogadoresRequest,
            List<Atleta> elenco) {
        if (jogadoresRequest == null || jogadoresRequest.isEmpty()) {
            removerTodosJogadores(tatica);
            return;
        }

        Map<Long, Atleta> elencoMap = elenco.stream()
                .collect(Collectors.toMap(Atleta::getAtletaId, Function.identity()));

        Map<Long, TaticaJogador> existentesPorAtleta = tatica.getJogadores().stream()
                .collect(Collectors.toMap(j -> j.getAtleta().getAtletaId(), Function.identity()));

        Set<Long> atletasNaRequest = new HashSet<>();

        for (TaticaJogadorRequestDTO jr : jogadoresRequest) {
            Atleta atleta = elencoMap.get(jr.getAtletaId());
            if (atleta == null) {
                throw new IllegalArgumentException(
                        "Atleta ID " + jr.getAtletaId() + " não pertence ao elenco do clube.");
            }

            atletasNaRequest.add(jr.getAtletaId());

            TaticaJogador jogador = existentesPorAtleta.get(jr.getAtletaId());
            if (jogador == null) {
                jogador = new TaticaJogador();
                jogador.setTaticaUsuarioClube(tatica);
                jogador.setAtleta(atleta);
                tatica.getJogadores().add(jogador);
            }

            jogador.setTipo(jr.getTipo());
            jogador.setPosicaoX(jr.getPosicaoX());
            jogador.setPosicaoY(jr.getPosicaoY());
            jogador.setOrdemReserva(jr.getOrdemReserva());
        }

        tatica.getJogadores().removeIf(j -> !atletasNaRequest.contains(j.getAtleta().getAtletaId()));
    }

    private void removerTodosJogadores(TaticaUsuarioClube tatica) {
        List<TaticaJogador> copia = new ArrayList<>(tatica.getJogadores());
        for (TaticaJogador jogador : copia) {
            tatica.getJogadores().remove(jogador);
        }
    }

    private void validarRequest(TaticaSalvarRequestDTO request, List<Atleta> elenco) {
        if (request.getJogadores() == null) {
            return;
        }

        Set<Long> atletaIds = new HashSet<>();
        int titulares = 0;

        Set<Long> elencoIds = elenco.stream().map(Atleta::getAtletaId).collect(Collectors.toSet());

        for (TaticaJogadorRequestDTO jr : request.getJogadores()) {
            if (jr.getAtletaId() == null) {
                throw new IllegalArgumentException("ID do atleta é obrigatório.");
            }
            if (!atletaIds.add(jr.getAtletaId())) {
                throw new IllegalArgumentException("Atleta duplicado na escalação: ID " + jr.getAtletaId());
            }
            if (!elencoIds.contains(jr.getAtletaId())) {
                throw new IllegalArgumentException(
                        "Atleta ID " + jr.getAtletaId() + " não pertence ao elenco do clube.");
            }
            if (jr.getTipo() == TipoTaticaJogador.TITULAR) {
                titulares++;
            }
        }

        if (titulares > MAX_TITULARES) {
            throw new IllegalArgumentException("Máximo de " + MAX_TITULARES + " titulares permitido.");
        }

        validarPreferenciaAtleta(request.getCapitaoAtletaId(), elencoIds, "capitão");
        validarPreferenciaAtleta(request.getBatedorPenaltisAtletaId(), elencoIds, "batedor de pênaltis");
        validarPreferenciaAtleta(request.getBatedorFaltaAtletaId(), elencoIds, "batedor de falta");
        validarPreferenciaAtleta(request.getBatedorEscanteioEsquerdoAtletaId(), elencoIds,
                "batedor de escanteio esquerdo");
        validarPreferenciaAtleta(request.getBatedorEscanteioDireitoAtletaId(), elencoIds,
                "batedor de escanteio direito");
    }

    private void validarPreferenciaAtleta(Long atletaId, Set<Long> elencoIds, String campo) {
        if (atletaId != null && !elencoIds.contains(atletaId)) {
            throw new IllegalArgumentException("Atleta de " + campo + " não pertence ao elenco do clube.");
        }
    }
}
