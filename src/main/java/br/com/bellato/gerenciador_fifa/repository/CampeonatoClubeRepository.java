package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;

@Repository
public interface CampeonatoClubeRepository extends JpaRepository<CampeonatoClube, Long> {

    List<CampeonatoClube> findByCampeonatoCampeonatoId(Long campeonatoId);
}
