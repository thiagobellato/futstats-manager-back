package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoSuspensao;

@Repository
public interface CampeonatoSuspensaoRepository extends JpaRepository<CampeonatoSuspensao, Long> {

    List<CampeonatoSuspensao> findByCampeonatoCampeonatoId(Long campeonatoId);

    List<CampeonatoSuspensao> findByCampeonatoCampeonatoIdAndAtivaTrue(Long campeonatoId);

    void deleteByCampeonatoCampeonatoId(Long campeonatoId);
}
