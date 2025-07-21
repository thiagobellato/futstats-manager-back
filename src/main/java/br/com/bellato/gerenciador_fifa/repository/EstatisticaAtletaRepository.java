package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;

@Repository
public interface EstatisticaAtletaRepository extends JpaRepository<EstatisticaAtleta, Long> {

     List<EstatisticaAtleta> findAll(Specification<EstatisticaAtleta> specification);
    
    // Retorna estatística específica com os relacionamentos carregados
    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube WHERE e.atleta.id = :atletaId AND e.clube.id = :clubeId")
    Optional<EstatisticaAtleta> findByAtletaIdAndClubeId(Long atletaId, Long clubeId);

    // Retorna todos com os relacionamentos carregados
    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube")
    List<EstatisticaAtleta> findAllWithAtletaAndClube();
}
