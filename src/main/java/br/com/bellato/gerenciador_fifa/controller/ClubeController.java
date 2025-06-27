package br.com.bellato.gerenciador_fifa.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.clube.ClubeRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.clube.ClubeMapper;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.service.ClubeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
// @CrossOrigin("*")
@RequestMapping("/api/clube")
@Tag(name = "Métodos do Clube")
public class ClubeController {
    @Autowired
    private ClubeService clubeService;

    @GetMapping
    @Operation(summary = "Método para listar todos os Clubes cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clubes encontrados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Clubes não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro ao listar os Clubes"),
            @ApiResponse(responseCode = "504", description = "Tempo da consulta esgotado"),
    })
    public ResponseEntity<List<ClubeResponseCompletoDTO>> obterTodos() {
        List<Clube> clubes = clubeService.obterTodos();
        List<ClubeResponseCompletoDTO> dtos = clubes.stream()
                .map(ClubeMapper::toDTOCompleto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Método para listar os Clubes por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clubes encontrados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "ID não encontrada"),
            @ApiResponse(responseCode = "404", description = "Clubes não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro ao listar os Clubes"),
            @ApiResponse(responseCode = "504", description = "Tempo da consulta esgotado"),
    })
    public ResponseEntity<ClubeResponseCompletoDTO> obterPorId(@PathVariable Long id) {
        Clube clube = clubeService.obterPorId(id); // Busca a entidade Clube pelo ID
        ClubeResponseCompletoDTO dto = ClubeMapper.toDTOCompleto(clube); // Converte a entidade para DTO
        return ResponseEntity.ok(dto); // Retorna o DTO para o cliente
    }

    @PostMapping("/adicionar")
    @Operation(summary = "Método para adicionar Clubes ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Clube adicionado com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Não foi possível adicionar o Clube"),
            @ApiResponse(responseCode = "500", description = "Erro ao adicionar o Clube"),
            @ApiResponse(responseCode = "504", description = "Tempo da operação esgotado"),
    })
    public ResponseEntity<ClubeResponseDTO> adicionar(@RequestBody ClubeRequestDTO dto) {
        ClubeResponseDTO response = clubeService.adicionar(dto);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/adicionar-em-lote")
    @Operation(summary = "Método para adicionar uma lista de Clubes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Clubes adicionados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Não foi possível adicionar os Clubes"),
            @ApiResponse(responseCode = "500", description = "Erro ao adicionar os Clubes"),
            @ApiResponse(responseCode = "504", description = "Tempo da operação esgotado"),
    })
    public ResponseEntity<List<ClubeResponseDTO>> adicionarEmLote(@RequestBody List<ClubeRequestDTO> clubes) {
        List<ClubeResponseDTO> clubesSalvos = clubeService.adicionarEmLote(clubes);
        return ResponseEntity.status(201).body(clubesSalvos);
    }

    @DeleteMapping("deletar/{id}")
    @Operation(summary = "Método para deletar Clubes ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Clube deletado com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Não foi possível deletar o Clube"),
            @ApiResponse(responseCode = "500", description = "Erro ao deletar o Clube"),
            @ApiResponse(responseCode = "504", description = "Tempo da operação esgotado"),
    })
    public ResponseEntity<Void> apagarPorId(@PathVariable Long id) {
        boolean apagado = clubeService.apagarPorId(id);
        if (apagado) {
            return ResponseEntity.ok().build(); // Retorna 200 OK sem corpo
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found
        }

    }

    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Atualiza um clube existente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clube atualizado com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Clube não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao atualizar o Clube"),
            @ApiResponse(responseCode = "504", description = "Tempo da operação esgotado")
    })
    public ResponseEntity<ClubeResponseCompletoDTO> atualizarPorId(
            @PathVariable Long id,
            @RequestBody ClubeRequestDTO dadosAtualizados) {

        ClubeResponseCompletoDTO clubeAtualizado = clubeService.atualizarPorId(id, dadosAtualizados);
        return ResponseEntity.ok(clubeAtualizado);
    }

}