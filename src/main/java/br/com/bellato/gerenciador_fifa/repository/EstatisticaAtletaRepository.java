package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;

@Repository
public interface EstatisticaAtletaRepository extends JpaRepository<EstatisticaAtleta, Long> {

    List<EstatisticaAtleta> findAll(Specification<EstatisticaAtleta> specification);

    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube WHERE e.atleta.atletaId = :atletaId AND e.clube.clubeId = :clubeId")
    Optional<EstatisticaAtleta> findByAtletaIdAndClubeId(@Param("atletaId") Long atletaId, @Param("clubeId") Long clubeId);

    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube")
    List<EstatisticaAtleta> findAllWithAtletaAndClube();

    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube WHERE e.atleta.atletaId = :atletaId AND e.dataFim IS NOT NULL")
    Optional<EstatisticaAtleta> findByAtletaIdAndDataFimIsNotNull(@Param("atletaId") Long atletaId);

    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube WHERE e.atleta.atletaId = :atletaId AND e.dataFim IS NULL")
    Optional<EstatisticaAtleta> findByAtletaIdAndDataFimIsNull(@Param("atletaId") Long atletaId);

    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube WHERE e.atleta.atletaId = :atletaId AND e.clube.clubeId = :clubeId AND e.dataFim IS NULL")
    Optional<EstatisticaAtleta> findEstatisticaAtiva(@Param("atletaId") Long atletaId, @Param("clubeId") Long clubeId);

    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube WHERE e.atleta.atletaId IN :atletaIds")
    List<EstatisticaAtleta> findByAtletaAtletaIdIn(@Param("atletaIds") List<Long> atletaIds);

    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube WHERE e.atleta.atletaId = :atletaId AND e.dataFim IS NULL")
    Optional<EstatisticaAtleta> findAtivaByAtletaId(@Param("atletaId") Long atletaId);

    @Query("SELECT e FROM EstatisticaAtleta e JOIN FETCH e.atleta JOIN FETCH e.clube WHERE e.atleta.atletaId = :atletaId")
    List<EstatisticaAtleta> findAllByAtletaId(@Param("atletaId") Long atletaId);

    @Query("""
            SELECT a.atletaId, a.nome, a.sobrenome, COALESCE(SUM(e.gols), 0)
            FROM Atleta a
            LEFT JOIN EstatisticaAtleta e ON e.atleta = a
            WHERE (:busca IS NULL OR :busca = ''
                   OR LOWER(CONCAT(COALESCE(a.nome, ''), ' ', COALESCE(a.sobrenome, ''))) LIKE LOWER(CONCAT('%', :busca, '%')))
            GROUP BY a.atletaId, a.nome, a.sobrenome
            ORDER BY COALESCE(SUM(e.gols), 0) DESC, a.nome ASC, a.sobrenome ASC
            """)
    Page<Object[]> rankingGlobalGols(@Param("busca") String busca, Pageable pageable);

    @Query("""
            SELECT a.atletaId, a.nome, a.sobrenome, COALESCE(SUM(e.assistencias), 0)
            FROM Atleta a
            LEFT JOIN EstatisticaAtleta e ON e.atleta = a
            WHERE (:busca IS NULL OR :busca = ''
                   OR LOWER(CONCAT(COALESCE(a.nome, ''), ' ', COALESCE(a.sobrenome, ''))) LIKE LOWER(CONCAT('%', :busca, '%')))
            GROUP BY a.atletaId, a.nome, a.sobrenome
            ORDER BY COALESCE(SUM(e.assistencias), 0) DESC, a.nome ASC, a.sobrenome ASC
            """)
    Page<Object[]> rankingGlobalAssistencias(@Param("busca") String busca, Pageable pageable);

    @Query("""
            SELECT a.atletaId, a.nome, a.sobrenome, COALESCE(SUM(e.golsContra), 0)
            FROM Atleta a
            LEFT JOIN EstatisticaAtleta e ON e.atleta = a
            WHERE (:busca IS NULL OR :busca = ''
                   OR LOWER(CONCAT(COALESCE(a.nome, ''), ' ', COALESCE(a.sobrenome, ''))) LIKE LOWER(CONCAT('%', :busca, '%')))
            GROUP BY a.atletaId, a.nome, a.sobrenome
            ORDER BY COALESCE(SUM(e.golsContra), 0) DESC, a.nome ASC, a.sobrenome ASC
            """)
    Page<Object[]> rankingGlobalGolsContra(@Param("busca") String busca, Pageable pageable);

    @Query("""
            SELECT a.atletaId, a.nome, a.sobrenome, COALESCE(SUM(e.cartaoAmarelo), 0)
            FROM Atleta a
            LEFT JOIN EstatisticaAtleta e ON e.atleta = a
            WHERE (:busca IS NULL OR :busca = ''
                   OR LOWER(CONCAT(COALESCE(a.nome, ''), ' ', COALESCE(a.sobrenome, ''))) LIKE LOWER(CONCAT('%', :busca, '%')))
            GROUP BY a.atletaId, a.nome, a.sobrenome
            ORDER BY COALESCE(SUM(e.cartaoAmarelo), 0) DESC, a.nome ASC, a.sobrenome ASC
            """)
    Page<Object[]> rankingGlobalAmarelos(@Param("busca") String busca, Pageable pageable);

    @Query("""
            SELECT a.atletaId, a.nome, a.sobrenome, COALESCE(SUM(e.cartaoVermelho), 0)
            FROM Atleta a
            LEFT JOIN EstatisticaAtleta e ON e.atleta = a
            WHERE (:busca IS NULL OR :busca = ''
                   OR LOWER(CONCAT(COALESCE(a.nome, ''), ' ', COALESCE(a.sobrenome, ''))) LIKE LOWER(CONCAT('%', :busca, '%')))
            GROUP BY a.atletaId, a.nome, a.sobrenome
            ORDER BY COALESCE(SUM(e.cartaoVermelho), 0) DESC, a.nome ASC, a.sobrenome ASC
            """)
    Page<Object[]> rankingGlobalVermelhos(@Param("busca") String busca, Pageable pageable);
}
