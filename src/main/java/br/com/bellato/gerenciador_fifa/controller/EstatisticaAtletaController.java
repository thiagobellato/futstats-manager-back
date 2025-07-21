package br.com.bellato.gerenciador_fifa.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.estatistica_atleta.EstatisticaAtletaRequestAtualizarDTO;
import br.com.bellato.gerenciador_fifa.dto.estatistica_atleta.EstatisticaAtletaResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.estatistica_atleta.EstatisticaAtletaMapper;
import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.service.EstatisticaAtletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
                        @ApiResponse(responseCode = "500", description = "Erro ao listar estatísticas"),
                        @ApiResponse(responseCode = "504", description = "Tempo da consulta esgotado"),
        })
        public ResponseEntity<List<EstatisticaAtletaResponseDTO>> obterTodas() {
                List<EstatisticaAtleta> estatisticaAtletas = estatisticaAtletaService.obterTodos();
                List<EstatisticaAtletaResponseDTO> dtos = estatisticaAtletas.stream()
                                .map(EstatisticaAtletaMapper::toDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @PutMapping("/atualizar")
        @Operation(summary = "Atualiza a estatística de um atleta para um clube específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estatística atualizada com sucesso!"),
                        @ApiResponse(responseCode = "404", description = "Estatística não encontrada"),
                        @ApiResponse(responseCode = "500", description = "Erro ao atualizar a estatística"),
                        @ApiResponse(responseCode = "504", description = "Tempo da operação esgotado"),
        })

        public ResponseEntity<String> atualizarEstatistica(@RequestBody EstatisticaAtletaRequestAtualizarDTO request) {
                try {
                        boolean atualizou = estatisticaAtletaService.atualizarEstatistica(
                                        request.getAtletaId(),
                                        request.getClubeId(),
                                        request.getGols(),
                                        request.getAssistencias());

                        if (atualizou) {
                                return ResponseEntity.ok("Estatística atualizada com sucesso!");
                        } else {
                                return ResponseEntity.status(404).body("Estatística não encontrada.");
                        }
                } catch (Exception e) {
                        return ResponseEntity.status(500).body("Erro ao atualizar a estatística: " + e.getMessage());
                }
        }

}
