package br.com.bellato.gerenciador_fifa.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.competitor
            WHERE u.username = :username
            """)
    Optional<User> findByUsernameComCompetitor(@Param("username") String username);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.competitor
            WHERE u.userId = :id
            """)
    Optional<User> findByIdComCompetitor(@Param("id") Long id);

    @Query(value = """
            SELECT u FROM User u
            LEFT JOIN u.competitor c
            WHERE u.enabled = true
              AND (:excludeUserId IS NULL OR u.userId <> :excludeUserId)
              AND (
                :q IS NULL OR :q = ''
                OR LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(c.displayName) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            ORDER BY u.username ASC
            """,
            countQuery = """
            SELECT COUNT(u) FROM User u
            LEFT JOIN u.competitor c
            WHERE u.enabled = true
              AND (:excludeUserId IS NULL OR u.userId <> :excludeUserId)
              AND (
                :q IS NULL OR :q = ''
                OR LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(c.displayName) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<User> buscar(
            @Param("q") String q,
            @Param("excludeUserId") Long excludeUserId,
            Pageable pageable);
}
