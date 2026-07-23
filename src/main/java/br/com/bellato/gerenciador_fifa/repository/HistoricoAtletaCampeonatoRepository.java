package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.HistoricoAtletaCampeonato;

@Repository
public interface HistoricoAtletaCampeonatoRepository extends JpaRepository<HistoricoAtletaCampeonato, Long> {

    @Query("""
            SELECT h FROM HistoricoAtletaCampeonato h
            JOIN FETCH h.campeonato c
            JOIN FETCH h.atleta
            WHERE h.atleta.atletaId = :atletaId
            ORDER BY c.dataFinalizacao DESC, c.dataCriacao DESC
            """)
    List<HistoricoAtletaCampeonato> findByAtletaIdOrderByCampeonatoDesc(@Param("atletaId") Long atletaId);

    @Query("""
            SELECT h FROM HistoricoAtletaCampeonato h
            JOIN FETCH h.campeonato
            JOIN FETCH h.atleta a
            LEFT JOIN FETCH a.clube
            """)
    List<HistoricoAtletaCampeonato> findAllComDetalhes();

    @Query("""
            SELECT h.atleta.atletaId, h.atleta.nome, h.atleta.sobrenome, COUNT(h)
            FROM HistoricoAtletaCampeonato h
            WHERE h.tituloConquistado = TRUE
              AND (:busca IS NULL OR :busca = ''
                   OR LOWER(CONCAT(COALESCE(h.atleta.nome, ''), ' ', COALESCE(h.atleta.sobrenome, '')))
                      LIKE LOWER(CONCAT('%', :busca, '%')))
            GROUP BY h.atleta.atletaId, h.atleta.nome, h.atleta.sobrenome
            ORDER BY COUNT(h) DESC, h.atleta.nome ASC, h.atleta.sobrenome ASC
            """)
    Page<Object[]> rankingTitulos(@Param("busca") String busca, Pageable pageable);
}
