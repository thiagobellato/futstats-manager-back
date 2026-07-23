package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.Atleta;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long>, JpaSpecificationExecutor<Atleta> {

    List<Atleta> findAll(Specification<Atleta> specification);

    List<Atleta> findByClubeClubeId(Long clubeId);

    @Query("SELECT a FROM Atleta a LEFT JOIN FETCH a.clube")
    List<Atleta> findAllComClube();

    @Query("SELECT a FROM Atleta a LEFT JOIN FETCH a.clube WHERE a.atletaId = :id")
    Optional<Atleta> findByIdComClube(@Param("id") Long id);
}
