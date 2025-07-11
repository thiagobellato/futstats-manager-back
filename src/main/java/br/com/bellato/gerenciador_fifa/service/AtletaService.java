package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaRequestAtualizarDTO;
import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.atleta.AtletaMapper;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AtletaService {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private ClubeRepository clubeRepository; // ✅ Corrigido aqui

    public List<Atleta> obterTodos() {
        List<Atleta> tipos = atletaRepository.findAll();

        return tipos
                .stream()
                .collect(Collectors.toList());
    }

    public Atleta obterPorId(long id) {
        return atletaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nenhum registro encontrado para o ID: " + id));
    }

    public AtletaResponseDTO adicionar(AtletaRequestDTO dto) {

        Clube clube = null;
        if (dto.getClubeId() != null) {
            clube = clubeRepository.findById(dto.getClubeId())
                    .orElseThrow(() -> new RuntimeException("Clube não encontrado com ID: " + dto.getClubeId()));
        }

        Atleta atleta = AtletaMapper.toEntity(dto, clube);
        Atleta salvo = atletaRepository.save(atleta);
        return AtletaMapper.toDTO(salvo);
    }

    public List<AtletaResponseDTO> adicionarEmLote(List<AtletaRequestDTO> dtos) {
    List<Atleta> atletas = dtos.stream()
            .map(dto -> {
                Clube clube = null;

                if (dto.getClubeId() != null) {
                    clube = clubeRepository.findById(dto.getClubeId())
                            .orElseThrow(() -> new RuntimeException("Clube não encontrado para o ID: " + dto.getClubeId()));
                }

                return AtletaMapper.toEntity(dto, clube);
            })
            .collect(Collectors.toList());

    List<Atleta> salvos = atletaRepository.saveAll(atletas);

    return salvos.stream()
            .map(AtletaMapper::toDTO)
            .collect(Collectors.toList());
}


    public boolean apagarPorId(Long id) {
        if (atletaRepository.existsById(id)) {
            atletaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public AtletaResponseCompletoDTO atualizarPorId(Long id, AtletaRequestAtualizarDTO dadosAtualizados) {
        Atleta atletaExistente = atletaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atleta não encontrado com o ID: " + id));

        if (dadosAtualizados.getNome() != null && !dadosAtualizados.getNome().isBlank()) {
            atletaExistente.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getSobrenome() != null && !dadosAtualizados.getSobrenome().isBlank()) {
            atletaExistente.setSobrenome(dadosAtualizados.getSobrenome());
        }
        if (dadosAtualizados.getDataDeNascimento() != null) {
            atletaExistente.setDataDeNascimento(dadosAtualizados.getDataDeNascimento());
        }
        if (dadosAtualizados.getNacionalidade() != null && !dadosAtualizados.getNacionalidade().isBlank()) {
            atletaExistente.setNacionalidade(dadosAtualizados.getNacionalidade());
        }
        if (dadosAtualizados.getPosicao() != null) {
            atletaExistente.setPosicao(dadosAtualizados.getPosicao());
        }
        if (dadosAtualizados.getClubeId() != null) {
            Clube clube = clubeRepository.findById(dadosAtualizados.getClubeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Clube não encontrado com o ID: " + dadosAtualizados.getClubeId()));
            atletaExistente.setClube(clube);
        }

        Atleta atualizado = atletaRepository.save(atletaExistente);
        return AtletaMapper.toDTOCompleto(atualizado);
    }
}