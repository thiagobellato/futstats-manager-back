package br.com.bellato.gerenciador_fifa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.service.EstatisticaAtletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/estatistica")
@Tag(name = "Métodos de Estatísticas do Atleta")
public class EstatisticaAtletaController {

    @Autowired
    private EstatisticaAtletaService estatisticaAtletaService;

    @GetMapping
    @Operation(summary = "Método para listar todas as estatísticas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas encontradas com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Nenhuma estatística encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro ao listar estatísticas")
    })
    public ResponseEntity<List<EstatisticaAtleta>> obterTodas() {
        return ResponseEntity.ok(estatisticaAtletaService.obterTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Método para listar estatística por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatística encontrada com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Estatística não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar estatística")
    })
    public ResponseEntity<EstatisticaAtleta> obterPorId(@PathVariable Long id) {
        try {
            EstatisticaAtleta estatistica = estatisticaAtletaService.obterPorId(id);
            return ResponseEntity.ok(estatistica);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/adicionar")
    @Operation(summary = "Método para adicionar estatística de atleta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estatística adicionada com sucesso!"),
            @ApiResponse(responseCode = "500", description = "Erro ao adicionar estatística")
    })
    public ResponseEntity<EstatisticaAtleta> adicionar(@RequestBody EstatisticaAtleta estatistica) {
        EstatisticaAtleta novaEstatistica = estatisticaAtletaService.adicionar(estatistica);
        return ResponseEntity.status(201).body(novaEstatistica);
    }

    @PatchMapping("/atualizar/{id}")
    @Operation(summary = "Método para atualizar parcialmente uma estatística")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatística atualizada com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Estatística não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro ao atualizar estatística")
    })
    public ResponseEntity<EstatisticaAtleta> atualizarParcialmente(@PathVariable Long id,
            @RequestBody EstatisticaAtleta atualizada) {
        try {
            EstatisticaAtleta estatisticaAtualizada = estatisticaAtletaService.atualizarParcialmente(id, atualizada);
            return ResponseEntity.ok(estatisticaAtualizada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(summary = "Método para deletar estatística de atleta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estatística deletada com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Estatística não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro ao deletar estatística")
    })
    public ResponseEntity<Void> apagarPorId(@PathVariable Long id) {
        boolean apagou = estatisticaAtletaService.apagarPorId(id);
        if (apagou) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
