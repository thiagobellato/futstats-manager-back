package br.com.bellato.gerenciador_fifa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.Campeonato;

@Repository
public interface CampeonatoRepository extends JpaRepository<Campeonato, Long> {

    @Query("""
            SELECT DISTINCT c FROM Campeonato c
            LEFT JOIN FETCH c.campeaoClube
            LEFT JOIN FETCH c.clubes
            WHERE c.campeonatoId = :id
            """)
    Optional<Campeonato> findByIdComClubesECampeao(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT c FROM Campeonato c
            LEFT JOIN FETCH c.distribuicaoRanks
            WHERE c.campeonatoId = :id
            """)
    Optional<Campeonato> findByIdComDistribuicao(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT c FROM Campeonato c
            LEFT JOIN FETCH c.rodadas
            WHERE c.campeonatoId = :id
            """)
    Optional<Campeonato> findByIdComRodadas(@Param("id") Long id);
}
