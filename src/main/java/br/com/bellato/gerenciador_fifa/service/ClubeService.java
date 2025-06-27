package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.dto.clube.ClubeRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.clube.ClubeMapper;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClubeService {
    @Autowired
    private ClubeRepository clubeRepository;

    public List<Clube> obterTodos() {

        List<Clube> tipos = clubeRepository.findAll();

        return tipos
                .stream()
                .collect(Collectors.toList());
    }

    public Clube obterPorId(long id) {

        Optional<Clube> optTipo = clubeRepository.findById(id);

        if (optTipo.isEmpty()) {
            throw new RuntimeException("Nenhum registro encontrado para o ID: " + id);
        }

        return optTipo.get();
    }

    public ClubeResponseDTO adicionar(ClubeRequestDTO dto) {

        Clube clube = ClubeMapper.toEntity(dto);
        Clube salvo = clubeRepository.save(clube);

        return ClubeMapper.toDTO(salvo);
    }

    public List<ClubeResponseDTO> adicionarEmLote(List<ClubeRequestDTO> dtos) {
        List<Clube> clubes = dtos.stream()
                .map(ClubeMapper::toEntity) // Converte cada DTO para entidade
                .collect(Collectors.toList());

        List<Clube> salvos = clubeRepository.saveAll(clubes); // Salva todos de uma vez

        return salvos.stream()
                .map(ClubeMapper::toDTO) // Converte os salvos para response DTOs
                .collect(Collectors.toList());
    }

    public boolean apagarPorId(Long id) {
        Optional<Clube> ClubeOptional = clubeRepository.findById(id);
        if (ClubeOptional.isPresent()) {
            clubeRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public ClubeResponseCompletoDTO atualizarPorId(Long id, ClubeRequestDTO dadosAtualizados) {
        Clube clubeExistente = clubeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado com o ID: " + id));

        if (dadosAtualizados.getNome() != null && !dadosAtualizados.getNome().isBlank()) {
            clubeExistente.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getPais() != null && !dadosAtualizados.getPais().isBlank()) {
            clubeExistente.setPais(dadosAtualizados.getPais());
        }
        if (dadosAtualizados.getSigla() != null && !dadosAtualizados.getSigla().isBlank()) {
            clubeExistente.setSigla(dadosAtualizados.getSigla());
        }

        Clube clubeAtualizado = clubeRepository.save(clubeExistente);

        // Aqui é onde usamos o mapper, em vez de um construtor
        return ClubeMapper.toDTOCompleto(clubeAtualizado);
    }
}
