package br.com.bellato.gerenciador_fifa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaCompletoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaSalvarRequestDTO;
import br.com.bellato.gerenciador_fifa.service.TaticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tatica")
@Tag(name = "Centro Tático")
public class TaticaController {

    @Autowired
    private TaticaService taticaService;

    @GetMapping("/clube/{clubeId}")
    @Operation(summary = "Obter configuração tática pessoal do usuário autenticado para um clube")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tática encontrada ou template vazio"),
            @ApiResponse(responseCode = "404", description = "Clube não encontrado"),
    })
    public ResponseEntity<TaticaCompletoResponseDTO> obterPorClube(@PathVariable Long clubeId) {
        return ResponseEntity.ok(taticaService.obterPorClube(clubeId));
    }

    @PutMapping("/clube/{clubeId}")
    @Operation(summary = "Salvar configuração tática pessoal do usuário autenticado para um clube")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tática salva com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Clube não encontrado"),
    })
    public ResponseEntity<TaticaCompletoResponseDTO> salvar(
            @PathVariable Long clubeId,
            @RequestBody TaticaSalvarRequestDTO request) {
        return ResponseEntity.ok(taticaService.salvar(clubeId, request));
    }

    @GetMapping("/{taticaId}")
    @Operation(summary = "Obter tática por ID (somente se pertence ao usuário autenticado)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tática encontrada"),
            @ApiResponse(responseCode = "400", description = "Sem permissão"),
            @ApiResponse(responseCode = "404", description = "Tática não encontrada"),
    })
    public ResponseEntity<TaticaCompletoResponseDTO> obterPorId(@PathVariable Long taticaId) {
        return ResponseEntity.ok(taticaService.obterPorId(taticaId));
    }
}
