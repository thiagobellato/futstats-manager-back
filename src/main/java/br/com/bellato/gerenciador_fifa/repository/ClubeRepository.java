package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.Clube;

@Repository
public interface ClubeRepository extends JpaRepository<Clube, Long>, JpaSpecificationExecutor<Clube> {

    List<Clube> findAll(Specification<Clube> specification);

    @Query("""
            SELECT c FROM Clube c
            LEFT JOIN FETCH c.estatistica e
            WHERE e IS NULL OR e.rank IS NULL
            """)
    List<Clube> findClubesSemRank();

    @Query("SELECT c FROM Clube c LEFT JOIN FETCH c.estatistica")
    List<Clube> findAllComEstatistica();

    @Query("SELECT c FROM Clube c LEFT JOIN FETCH c.estatistica WHERE c.clubeId = :id")
    Optional<Clube> findByIdComEstatistica(@Param("id") Long id);
}
