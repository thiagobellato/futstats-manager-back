package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.model.Clube;

@Repository
public interface ClubeRepository extends JpaRepository<Clube, Long>, JpaSpecificationExecutor<Clube> {

    List<Clube> findAll(Specification<Clube> specification);

    List<Clube> findByRankIsNull();

    List<Clube> findByRank(ClubRank rank);

    long countByRank(ClubRank rank);

    @Query("SELECT c.rank, COUNT(c) FROM Clube c WHERE c.rank IS NOT NULL GROUP BY c.rank")
    List<Object[]> contarAgrupadoPorRank();
}
