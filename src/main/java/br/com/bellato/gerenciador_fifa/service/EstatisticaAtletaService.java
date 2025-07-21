package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaAtletaRepository;

@Service
public class EstatisticaAtletaService {

    @Autowired
    private EstatisticaAtletaRepository estatisticaRepository;

    public List<EstatisticaAtleta> obterTodos() {
        List<EstatisticaAtleta> tipos = estatisticaRepository.findAll();

        return tipos.stream().collect(Collectors.toList());

    }

    public boolean atualizarEstatistica(Long atletaId, Long clubeId, Integer gols, Integer assistencias) {
        Optional<EstatisticaAtleta> optionalEstatistica = estatisticaRepository.findByAtletaIdAndClubeId(atletaId,
                clubeId);

        if (optionalEstatistica.isPresent()) {
            EstatisticaAtleta estatistica = optionalEstatistica.get();
            estatistica.setGols(gols);
            estatistica.setAssistencias(assistencias);
            estatisticaRepository.save(estatistica);
            return true;
        }

        return false;
    }

    // public EstatisticaAtleta obterPorId(Long atletaId) {
    // return estatisticaRepository.findById(atletaId)
    // .orElseThrow(() -> new EntityNotFoundException("Estatística não encontrada
    // com o ID: " + atletaId));
    // }

    // public EstatisticaAtletaResponseDTO adicionar(EstatisticaAtletaRequestDTO
    // estatistica) {

    // Atleta a = atletaRepository.findById(estatistica.getAtletaId())
    // .orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID: " +
    // estatistica.getAtletaId()));

    // Clube c = clubeRepository.findById(estatistica.getClubeId())
    // .orElseThrow(() -> new RuntimeException("Clube não encontrado com ID: " +
    // estatistica.getClubeId()));

    // EstatisticaAtleta estatisticaAtleta =
    // EstatisticaAtletaMapper.toEntity(estatistica, c, a);
    // EstatisticaAtleta salvo = estatisticaRepository.save(estatisticaAtleta);
    // return EstatisticaAtletaMapper.toDTO(salvo);
    // }

    // /**
    // * @param id
    // * @param atualizada
    // * @return
    // */
    // public EstatisticaAtletaResponseDTO atualizar(Long id,
    // EstatisticaAtletaRequestDTO dto) {
    // EstatisticaAtleta existente = estatisticaRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException("Estatística não encontrada
    // com o ID: " + id));

    // // Atualiza valores numéricos, se não forem nulos
    // if (dto.getGols() != null) {
    // existente.setGols(dto.getGols());
    // }

    // if (dto.getAssistencias() != null) {
    // existente.setAssistencias(dto.getAssistencias());
    // }

    // // Atualiza relacionamento com Atleta, se necessário
    // if (dto.getAtletaId() != null) {
    // Atleta atleta = atletaRepository.findById(dto.getAtletaId())
    // .orElseThrow(
    // () -> new EntityNotFoundException("Atleta não encontrado com o ID: " +
    // dto.getAtletaId()));
    // existente.setAtleta(atleta);
    // }

    // // Atualiza relacionamento com Clube, se necessário
    // if (dto.getClubeId() != null) {
    // Clube clube = clubeRepository.findById(dto.getClubeId())
    // .orElseThrow(
    // () -> new EntityNotFoundException("Clube não encontrado com o ID: " +
    // dto.getClubeId()));
    // existente.setClube(clube);
    // }

    // EstatisticaAtleta salvo = estatisticaRepository.save(existente);
    // return EstatisticaAtletaMapper.toDTO(salvo);
    // }

    // public boolean apagarPorId(Long id) {
    // Optional<EstatisticaAtleta> estatisticaOptional =
    // estatisticaRepository.findById(id);
    // if (estatisticaOptional.isPresent()) {
    // estatisticaRepository.deleteById(id);
    // return true;
    // } else {
    // return false;
    // }
    // }

    // public EstatisticaAtletaResponseDTO buscarPorAtletaEClube(Long atletaId, Long
    // clubeId) {
    // EstatisticaAtleta entity =
    // estatisticaRepository.findByAtletaIdAndClubeId(atletaId, clubeId)
    // .orElseThrow(() -> new EntityNotFoundException(
    // "Estatística não encontrada para o atleta e clube informados"));

    // return EstatisticaAtletaMapper.toDTO(entity);
    // }

    // public EstatisticaAtletaResponseDTO
    // atualizarPorAtletaEClube(EstatisticaAtletaRequestDTO dto) {
    // EstatisticaAtleta existente = estatisticaRepository
    // .findByAtletaIdAndClubeId(dto.getAtletaId(), dto.getClubeId())
    // .orElseThrow(() -> new EntityNotFoundException("Estatística não encontrada
    // para atletaId: "
    // + dto.getAtletaId() + " e clubeId: " + dto.getClubeId()));

    // if (dto.getGols() != null) {
    // existente.setGols(dto.getGols());
    // }

    // if (dto.getAssistencias() != null) {
    // existente.setAssistencias(dto.getAssistencias());
    // }

    // EstatisticaAtleta salvo = estatisticaRepository.save(existente);
    // return EstatisticaAtletaMapper.toDTO(salvo);
    // }

}
