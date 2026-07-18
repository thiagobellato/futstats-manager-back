package br.com.bellato.gerenciador_fifa.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoCriarRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoValidacaoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClubesPorRankResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.campeonato.CampeonatoMapper;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.service.CampeonatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/campeonato")
@Tag(name = "Métodos do Campeonato")
public class CampeonatoController {

    @Autowired
    private CampeonatoService campeonatoService;

    @GetMapping
    @Operation(summary = "Listar todos os campeonatos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campeonatos encontrados com sucesso!"),
            @ApiResponse(responseCode = "500", description = "Erro ao listar os campeonatos")
    })
    public ResponseEntity<List<CampeonatoResponseDTO>> obterTodos() {
        List<CampeonatoResponseDTO> dtos = campeonatoService.obterTodos().stream()
                .map(CampeonatoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar campeonato por ID com detalhes completos")
    public ResponseEntity<CampeonatoResponseCompletoDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(campeonatoService.obterCompletoPorId(id));
    }

    @GetMapping("/clubes-por-rank")
    @Operation(summary = "Listar clubes globais agrupados por rank para seleção")
    public ResponseEntity<ClubesPorRankResponseDTO> obterClubesPorRank() {
        return ResponseEntity.ok(campeonatoService.obterClubesPorRank());
    }

    @PostMapping("/validar")
    @Operation(summary = "Validar dados do campeonato antes da criação")
    public ResponseEntity<CampeonatoValidacaoResponseDTO> validar(@RequestBody CampeonatoCriarRequestDTO dto) {
        return ResponseEntity.ok(campeonatoService.validar(dto));
    }

    @PostMapping("/criar")
    @Operation(summary = "Criar campeonato com snapshots de clubes e atletas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Campeonato criado com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para criação")
    })
    public ResponseEntity<CampeonatoResponseDTO> criar(@RequestBody CampeonatoCriarRequestDTO dto) {
        CampeonatoResponseDTO response = campeonatoService.criar(dto);
        return ResponseEntity.status(201).body(response);
    }
}
