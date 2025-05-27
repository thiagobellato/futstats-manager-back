package br.com.bellato.gerenciador_fifa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.service.AtletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/atleta")
@Tag(name = "Métodos do Atleta")
public class AtletaController {
    @Autowired
    private AtletaService atletaService;

    @GetMapping
    @Operation(summary = "Método para listar todos os Atletas cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atletas encontrados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Atletas não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro ao listar os Atletas"),
            @ApiResponse(responseCode = "504", description = "Tempo da consulta esgotado"),
    })
    public ResponseEntity<List<Atleta>> obterTodos() {

        return ResponseEntity.ok(atletaService.obterTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Método para listar os atletas por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atletas encontrados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "ID não encontrada"),
            @ApiResponse(responseCode = "404", description = "Atletas não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro ao listar os Atletas"),
            @ApiResponse(responseCode = "504", description = "Tempo da consulta esgotado"),
    })
    public ResponseEntity<Atleta> obterPorId(@PathVariable Long id) {

        return ResponseEntity.ok(atletaService.obterPorId(id));
    }

    @PostMapping("/adicionar")
    @Operation(summary = "Método para adicionar Atletas ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atleta adicionado com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Não foi possível adicionar o Atleta"),
            @ApiResponse(responseCode = "500", description = "Erro ao adicionar o Atleta"),
            @ApiResponse(responseCode = "504", description = "Tempo da operação esgotado"),
    })
    public ResponseEntity<Atleta> adicionar(@RequestBody Atleta atleta) {
        atletaService.adicionar(atleta);

        // Atleta atletaAdicionado = atletaService.adicionar(atleta);

        return ResponseEntity.status(201).body(atleta);
    }

    @DeleteMapping("deletar/{id}")
    @Operation(summary = "Método para deletar Atletas ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atleta deletado com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Não foi possível deletar o Atleta"),
            @ApiResponse(responseCode = "500", description = "Erro ao deletar o Atleta"),
            @ApiResponse(responseCode = "504", description = "Tempo da operação esgotado"),
    })
    public ResponseEntity<Void> apagarPorId(@PathVariable Long id) {
        boolean apagado = atletaService.apagarPorId(id);
        if (apagado) {
            return ResponseEntity.ok().build(); // Retorna 200 OK sem corpo
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found
        }

    }

    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Método para atualizar Atletas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atleta atualizado com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Atleta não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao atualizar o Atleta"),
            @ApiResponse(responseCode = "504", description = "Tempo da operação esgotado"),
    })
    public ResponseEntity<Atleta> atualizarPorId(@PathVariable Long id, @RequestBody Atleta dadosAtualizados) {
        Atleta atletaAtualizado = atletaService.atualizarPorId(id, dadosAtualizados);
        return ResponseEntity.ok(atletaAtualizado);
    }

}