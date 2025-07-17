package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.dto.estatistica_atleta.EstatisticaAtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.estatistica_atleta.EstatisticaAtletaResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.estatistica_atleta.EstatisticaAtletaMapper;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaAtletaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EstatisticaAtletaService {

    @Autowired
    private EstatisticaAtletaRepository estatisticaRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private ClubeRepository clubeRepository;

    public List<EstatisticaAtleta> obterTodos() {
        List<EstatisticaAtleta> tipos = estatisticaRepository.findAll();

        return tipos.stream().collect(Collectors.toList());

    }

    public EstatisticaAtleta obterPorId(Long atletaId) {
        return estatisticaRepository.findById(atletaId)
                .orElseThrow(() -> new EntityNotFoundException("Estatística não encontrada com o ID: " + atletaId));
    }

    public EstatisticaAtletaResponseDTO adicionar(EstatisticaAtletaRequestDTO estatistica) {

        Atleta a = atletaRepository.findById(estatistica.getAtleta_id())
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID: " + estatistica.getAtleta_id()));

        Clube c = clubeRepository.findById(estatistica.getClube_id())
                .orElseThrow(() -> new RuntimeException("Clube não encontrado com ID: " + estatistica.getClube_id()));

        EstatisticaAtleta estatisticaAtleta = EstatisticaAtletaMapper.toEntity(estatistica, c, a);
        EstatisticaAtleta salvo = estatisticaRepository.save(estatisticaAtleta);
        return EstatisticaAtletaMapper.toDTO(salvo);
    }

    /**
     * @param id
     * @param atualizada
     * @return
     */
    public EstatisticaAtleta atualizarParcialmente(Long id, EstatisticaAtleta atualizada) {
        EstatisticaAtleta existente = estatisticaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estatística não encontrada com o ID: " + id));

        // Atualização condicional com verificação de nulos
        // if (atualizada.getAtleta() != null) {
        // existente.setAtleta(atualizada.getAtleta());
        // }
        if (atualizada.getClube() != null) {
            existente.setClube(atualizada.getClube());
        }
        if (atualizada.getGols() != null) {
            existente.setGols(atualizada.getGols());
        }
        if (atualizada.getAssistencias() != null) {
            existente.setAssistencias(atualizada.getAssistencias());
        }
        if (atualizada.getDataInicio() != null) {
            existente.setDataInicio(atualizada.getDataInicio());
        }
        if (atualizada.getDataFim() != null) {
            existente.setDataFim(atualizada.getDataFim());
        }

        return estatisticaRepository.save(existente);
    }

    public boolean apagarPorId(Long id) {
        Optional<EstatisticaAtleta> estatisticaOptional = estatisticaRepository.findById(id);
        if (estatisticaOptional.isPresent()) {
            estatisticaRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
