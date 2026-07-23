package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.clube.ClubeRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.clube.ClubeMapper;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.EstatisticaClube;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaClubeRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClubeService {
    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private EstatisticaClubeRepository estatisticaClubeRepository;

    @Autowired
    private ClubeRankService clubeRankService;

    public List<Clube> obterTodos() {
        return clubeRepository.findAllComEstatistica();
    }

    public Clube obterPorId(long id) {
        return clubeRepository.findByIdComEstatistica(id)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado com o ID: " + id));
    }

    @Transactional
    public ClubeResponseDTO adicionar(ClubeRequestDTO dto) {
        Clube clube = ClubeMapper.toEntity(dto);
        if (clube.getEstatistica() == null) {
            clube.setEstatistica(EstatisticaClube.zerada(clube));
        }
        Clube salvo = clubeRepository.save(clube);
        clubeRankService.atribuirRankSeNecessario(salvo);

        return ClubeMapper.toDTO(salvo);
    }

    @Transactional
    public List<ClubeResponseDTO> adicionarEmLote(List<ClubeRequestDTO> dtos) {
        List<Clube> clubes = dtos.stream()
                .map(ClubeMapper::toEntity)
                .peek(clube -> {
                    if (clube.getEstatistica() == null) {
                        clube.setEstatistica(EstatisticaClube.zerada(clube));
                    }
                })
                .collect(Collectors.toList());

        List<Clube> salvos = clubeRepository.saveAll(clubes);
        clubeRankService.atribuirRanksSeNecessario(salvos);

        return salvos.stream()
                .map(ClubeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean apagarPorId(Long id) {
        Optional<Clube> clubeOptional = clubeRepository.findById(id);
        if (clubeOptional.isPresent()) {
            clubeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public ClubeResponseCompletoDTO atualizarPorId(Long id, ClubeRequestDTO dadosAtualizados) {
        Clube clubeExistente = clubeRepository.findByIdComEstatistica(id)
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
        if (dadosAtualizados.getRank() != null) {
            clubeExistente.setRank(dadosAtualizados.getRank());
        }

        garantirEstatistica(clubeExistente);
        Clube clubeAtualizado = clubeRepository.save(clubeExistente);
        return ClubeMapper.toDTOCompleto(clubeAtualizado);
    }

    @Transactional
    private EstatisticaClube obterOuCriarEstatistica(Clube clube) {
        if (clube.getEstatistica() != null) {
            return clube.getEstatistica();
        }
        return estatisticaClubeRepository.findByClubeClubeId(clube.getClubeId())
                .orElseGet(() -> {
                    EstatisticaClube criada = EstatisticaClube.zerada(clube);
                    clube.setEstatistica(criada);
                    return estatisticaClubeRepository.save(criada);
                });
    }

    private void garantirEstatistica(Clube clube) {
        if (clube.getEstatistica() == null) {
            obterOuCriarEstatistica(clube);
        }
    }
}
