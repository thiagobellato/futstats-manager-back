package br.com.bellato.gerenciador_fifa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.service.EstatisticaAtletaService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/estatisticas-atleta")
public class EstatisticaAtletaController {

    @Autowired
    private EstatisticaAtletaService estatisticaAtletaService;

    // Listar todas as estatísticas
    @GetMapping
    public ResponseEntity<List<EstatisticaAtleta>> obterTodas() {
        List<EstatisticaAtleta> lista = estatisticaAtletaService.obterTodas();
        return ResponseEntity.ok(lista);
    }

    // Obter estatística por ID
    @GetMapping("/{id}")
    public ResponseEntity<EstatisticaAtleta> obterPorId(@PathVariable Long id) {
        try {
            EstatisticaAtleta estatistica = estatisticaAtletaService.obterPorId(id);
            return ResponseEntity.ok(estatistica);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Criar nova estatística (POST)
    @PostMapping
    public ResponseEntity<EstatisticaAtleta> adicionar(@RequestBody EstatisticaAtleta estatistica) {
        EstatisticaAtleta novaEstatistica = estatisticaAtletaService.adicionar(estatistica);
        return ResponseEntity.ok(novaEstatistica);
    }

    // Atualizar parcialmente (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<EstatisticaAtleta> atualizarParcialmente(@PathVariable Long id,
            @RequestBody EstatisticaAtleta atualizada) {
        try {
            EstatisticaAtleta estatisticaAtualizada = estatisticaAtletaService.atualizarParcialmente(id, atualizada);
            return ResponseEntity.ok(estatisticaAtualizada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Apagar estatística por ID (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPorId(@PathVariable Long id) {
        boolean apagou = estatisticaAtletaService.apagarPorId(id);
        if (apagou) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
