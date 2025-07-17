package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;

@Repository
public interface EstatisticaAtletaRepository extends JpaRepository<EstatisticaAtleta, Long> {
    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube")
    List<EstatisticaAtleta> findAllWithAtletaAndClube();

}
