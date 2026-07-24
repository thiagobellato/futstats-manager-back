package br.com.bellato.gerenciador_fifa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.Competitor;

@Repository
public interface CompetitorRepository extends JpaRepository<Competitor, Long> {

    Optional<Competitor> findByUser_UserId(Long userId);
}
