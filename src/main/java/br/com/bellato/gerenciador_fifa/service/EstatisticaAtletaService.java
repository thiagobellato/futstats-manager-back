package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.dto.estatistica_atleta.EstatisticaAtletaResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.estatistica_atleta.EstatisticaAtletaMapper;
import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaAtletaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EstatisticaAtletaService {

    @Autowired
    private EstatisticaAtletaRepository estatisticaRepository;

    public List<EstatisticaAtleta> obterTodos() {
        return estatisticaRepository.findAllWithAtletaAndClube();
    }

    public EstatisticaAtletaResponseDTO buscarEstatisticaPorAtletaEClube(Long atletaId, Long clubeId) {
        Optional<EstatisticaAtleta> estatisticaOptional = estatisticaRepository.findEstatisticaAtiva(atletaId, clubeId);

        if (estatisticaOptional.isEmpty()) {
            throw new EntityNotFoundException("Não há estatística ativa para esse atleta nesse clube.");
        }

        EstatisticaAtleta estatistica = estatisticaOptional.get();
        return EstatisticaAtletaMapper.toDTO(estatistica);
    }

    public boolean atualizarEstatistica(Long atletaId, Long clubeId, Integer gols, Integer assistencias,
            Integer cartaoAmarelo, Integer cartaoVermelho, Integer golsContra) {
        Optional<EstatisticaAtleta> optionalEstatistica = estatisticaRepository.findEstatisticaAtiva(atletaId, clubeId);

        if (optionalEstatistica.isPresent()) {
            EstatisticaAtleta estatistica = optionalEstatistica.get();
            estatistica.setGols(gols);
            estatistica.setAssistencias(assistencias);
            estatistica.setCartaoAmarelo(cartaoAmarelo);
            estatistica.setCartaoVermelho(cartaoVermelho);
            if (golsContra != null) {
                estatistica.setGolsContra(golsContra);
            }
            estatisticaRepository.save(estatistica);
            return true;
        }

        return false;
    }

}
