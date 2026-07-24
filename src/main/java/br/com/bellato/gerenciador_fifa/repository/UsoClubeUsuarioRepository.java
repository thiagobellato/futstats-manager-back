package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.UsoClubeUsuario;

@Repository
public interface UsoClubeUsuarioRepository extends JpaRepository<UsoClubeUsuario, Long> {

    Optional<UsoClubeUsuario> findByUsuarioUserIdAndClubeClubeId(Long userId, Long clubeId);

    List<UsoClubeUsuario> findByUsuarioUserId(Long userId);

    @Query("""
            SELECT u FROM UsoClubeUsuario u
            JOIN FETCH u.clube
            WHERE u.usuario.userId = :userId
            """)
    List<UsoClubeUsuario> findByUsuarioIdComClube(@Param("userId") Long userId);

    long countByUsuarioUserId(Long userId);
}
