package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.Atleta;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long>, JpaSpecificationExecutor<Atleta> {

    List<Atleta> findAll(Specification<Atleta> specification);
}
