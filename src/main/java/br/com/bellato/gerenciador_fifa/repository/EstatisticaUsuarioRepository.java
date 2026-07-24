package br.com.bellato.gerenciador_fifa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.EstatisticaUsuario;

@Repository
public interface EstatisticaUsuarioRepository extends JpaRepository<EstatisticaUsuario, Long> {

    Optional<EstatisticaUsuario> findByUsuarioUserId(Long userId);

    @Query("""
            SELECT s FROM EstatisticaUsuario s
            LEFT JOIN FETCH s.clubeFavorito
            LEFT JOIN FETCH s.clubeMaisTitulos
            LEFT JOIN FETCH s.usuario u
            LEFT JOIN FETCH u.competitor
            WHERE u.userId = :userId
            """)
    Optional<EstatisticaUsuario> findByUsuarioIdComClubes(@Param("userId") Long userId);
}
