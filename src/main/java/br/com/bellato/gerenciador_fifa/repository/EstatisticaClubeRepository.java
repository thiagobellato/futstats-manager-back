package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.model.EstatisticaClube;

@Repository
public interface EstatisticaClubeRepository extends JpaRepository<EstatisticaClube, Long> {

    Optional<EstatisticaClube> findByClubeClubeId(Long clubeId);

    @Query("SELECT e FROM EstatisticaClube e JOIN FETCH e.clube WHERE e.clube.clubeId = :clubeId")
    Optional<EstatisticaClube> findByClubeIdComClube(@Param("clubeId") Long clubeId);

    @Query("SELECT e FROM EstatisticaClube e JOIN FETCH e.clube WHERE e.clube.clubeId IN :clubeIds")
    List<EstatisticaClube> findByClubeClubeIdIn(@Param("clubeIds") List<Long> clubeIds);

    long countByRank(ClubRank rank);

    @Query("SELECT e.rank, COUNT(e) FROM EstatisticaClube e WHERE e.rank IS NOT NULL GROUP BY e.rank")
    List<Object[]> contarAgrupadoPorRank();

    @Query("""
            SELECT c.clubeId, c.nome, c.sigla, COALESCE(e.titulos, 0)
            FROM Clube c
            LEFT JOIN EstatisticaClube e ON e.clube = c
            WHERE (:busca IS NULL OR :busca = ''
                   OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(COALESCE(c.sigla, '')) LIKE LOWER(CONCAT('%', :busca, '%')))
            ORDER BY COALESCE(e.titulos, 0) DESC, c.nome ASC
            """)
    Page<Object[]> rankingPorTitulos(@Param("busca") String busca, Pageable pageable);

    @Query("""
            SELECT c.clubeId, c.nome, c.sigla,
                   COALESCE(e.vitorias, 0), COALESCE(e.empates, 0), COALESCE(e.derrotas, 0)
            FROM Clube c
            JOIN EstatisticaClube e ON e.clube = c
            WHERE (COALESCE(e.vitorias, 0) + COALESCE(e.empates, 0) + COALESCE(e.derrotas, 0)) >= 3
              AND (:busca IS NULL OR :busca = ''
                   OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(COALESCE(c.sigla, '')) LIKE LOWER(CONCAT('%', :busca, '%')))
            ORDER BY
              ((COALESCE(e.vitorias, 0) * 3.0 + COALESCE(e.empates, 0))
                / ((COALESCE(e.vitorias, 0) + COALESCE(e.empates, 0) + COALESCE(e.derrotas, 0)) * 3.0)) DESC,
              c.nome ASC
            """)
    Page<Object[]> rankingPorAproveitamento(@Param("busca") String busca, Pageable pageable);
}
