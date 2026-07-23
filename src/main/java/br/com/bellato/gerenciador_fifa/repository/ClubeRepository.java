package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
            SELECT c FROM Clube c
            WHERE (:busca IS NULL OR :busca = ''
                   OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(COALESCE(c.sigla, '')) LIKE LOWER(CONCAT('%', :busca, '%')))
            ORDER BY COALESCE(c.titulos, 0) DESC, c.nome ASC
            """)
    Page<Clube> rankingPorTitulos(@Param("busca") String busca, Pageable pageable);

    @Query("""
            SELECT c FROM Clube c
            WHERE (COALESCE(c.vitorias, 0) + COALESCE(c.empates, 0) + COALESCE(c.derrotas, 0)) >= 3
              AND (:busca IS NULL OR :busca = ''
                   OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(COALESCE(c.sigla, '')) LIKE LOWER(CONCAT('%', :busca, '%')))
            ORDER BY
              ((COALESCE(c.vitorias, 0) * 3.0 + COALESCE(c.empates, 0))
                / ((COALESCE(c.vitorias, 0) + COALESCE(c.empates, 0) + COALESCE(c.derrotas, 0)) * 3.0)) DESC,
              c.nome ASC
            """)
    Page<Clube> rankingPorAproveitamento(@Param("busca") String busca, Pageable pageable);
}
