package br.com.bellato.gerenciador_fifa.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.RivalidadeUsuario;

@Repository
public interface RivalidadeUsuarioRepository extends JpaRepository<RivalidadeUsuario, Long> {

    Optional<RivalidadeUsuario> findByUsuarioAUserIdAndUsuarioBUserId(Long userAId, Long userBId);

    @Query(value = """
            SELECT r FROM RivalidadeUsuario r
            JOIN r.usuarioA a
            JOIN r.usuarioB b
            WHERE (a.userId = :userId OR b.userId = :userId)
              AND (
                :q IS NULL OR :q = ''
                OR LOWER(a.username) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(b.username) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """,
            countQuery = """
            SELECT COUNT(r) FROM RivalidadeUsuario r
            JOIN r.usuarioA a
            JOIN r.usuarioB b
            WHERE (a.userId = :userId OR b.userId = :userId)
              AND (
                :q IS NULL OR :q = ''
                OR LOWER(a.username) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(b.username) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<RivalidadeUsuario> buscarPorUsuario(
            @Param("userId") Long userId,
            @Param("q") String q,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT r FROM RivalidadeUsuario r
            JOIN FETCH r.usuarioA a
            JOIN FETCH r.usuarioB b
            LEFT JOIN FETCH a.competitor
            LEFT JOIN FETCH b.competitor
            LEFT JOIN FETCH r.ultimoVencedor
            LEFT JOIN FETCH r.sequenciaAtualUsuario
            WHERE r.rivalidadeUsuarioId IN :ids
            """)
    java.util.List<RivalidadeUsuario> findAllByIdComUsuarios(@Param("ids") java.util.Collection<Long> ids);

    @Query("""
            SELECT r FROM RivalidadeUsuario r
            JOIN FETCH r.usuarioA a
            JOIN FETCH r.usuarioB b
            LEFT JOIN FETCH a.competitor
            LEFT JOIN FETCH b.competitor
            LEFT JOIN FETCH r.ultimoVencedor
            LEFT JOIN FETCH r.sequenciaAtualUsuario
            WHERE r.rivalidadeUsuarioId = :id
            """)
    Optional<RivalidadeUsuario> findByIdComUsuarios(@Param("id") Long id);
}
