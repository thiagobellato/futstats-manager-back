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

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoComposicaoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoCriarRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoEstatisticasDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoValidacaoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.ClubesPorRankResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.DefinirVencedorRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.PartidaDetalheResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RegistrarPartidaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RemanejamentoInfoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.RemanejamentoRequestDTO;
import br.com.bellato.gerenciador_fifa.mapper.campeonato.CampeonatoMapper;
import br.com.bellato.gerenciador_fifa.service.CampeonatoMotorService;
import br.com.bellato.gerenciador_fifa.service.CampeonatoPartidaService;
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

    @Autowired
    private CampeonatoMotorService campeonatoMotorService;

    @Autowired
    private CampeonatoPartidaService campeonatoPartidaService;

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

    @GetMapping("/clubes-por-rank")
    @Operation(summary = "Listar clubes globais agrupados por rank para seleção")
    public ResponseEntity<ClubesPorRankResponseDTO> obterClubesPorRank(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long campeaoAnteriorClubeId) {
        return ResponseEntity.ok(campeonatoService.obterClubesPorRank(campeaoAnteriorClubeId));
    }

    @GetMapping("/composicao")
    @Operation(summary = "Calcular composição de vagas do campeonato (incluindo campeão protegido)")
    public ResponseEntity<CampeonatoComposicaoResponseDTO> obterComposicao(
            @org.springframework.web.bind.annotation.RequestParam Integer quantidadeClubes,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "false") Boolean possuiCampeaoAnterior,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long campeaoAnteriorClubeId) {
        return ResponseEntity.ok(
                campeonatoService.obterComposicao(quantidadeClubes, possuiCampeaoAnterior, campeaoAnteriorClubeId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar campeonato por ID com detalhes completos")
    public ResponseEntity<CampeonatoResponseCompletoDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(campeonatoService.obterCompletoPorId(id));
    }

    @PostMapping("/validar")
    @Operation(summary = "Validar dados do campeonato antes da criação")
    public ResponseEntity<CampeonatoValidacaoResponseDTO> validar(@RequestBody CampeonatoCriarRequestDTO dto) {
        return ResponseEntity.ok(campeonatoService.validar(dto));
    }

    @PostMapping("/criar")
    @Operation(summary = "Criar campeonato, sortear clubes e gerar a primeira rodada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Campeonato criado com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para criação")
    })
    public ResponseEntity<CampeonatoResponseDTO> criar(@RequestBody CampeonatoCriarRequestDTO dto) {
        CampeonatoResponseDTO response = campeonatoService.criar(dto);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar campeonato existente (sorteio + primeira rodada)")
    public ResponseEntity<CampeonatoResponseCompletoDTO> iniciar(@PathVariable Long id) {
        campeonatoMotorService.iniciarPorId(id);
        return ResponseEntity.ok(campeonatoService.obterCompletoPorId(id));
    }

    @PostMapping("/{id}/partida/{partidaId}/definir-vencedor")
    @Operation(summary = "Definir o clube vencedor de uma partida e evoluir o campeonato")
    public ResponseEntity<CampeonatoResponseCompletoDTO> definirVencedor(
            @PathVariable Long id,
            @PathVariable Long partidaId,
            @RequestBody DefinirVencedorRequestDTO request) {
        campeonatoMotorService.definirVencedor(id, partidaId, request);
        return ResponseEntity.ok(campeonatoService.obterCompletoPorId(id));
    }

    @GetMapping("/{id}/partida/{partidaId}")
    @Operation(summary = "Obter detalhe da partida com atletas e eventos")
    public ResponseEntity<PartidaDetalheResponseDTO> obterPartida(
            @PathVariable Long id,
            @PathVariable Long partidaId) {
        return ResponseEntity.ok(campeonatoPartidaService.obterDetalhe(id, partidaId));
    }

    @PostMapping("/{id}/partida/{partidaId}/registrar")
    @Operation(summary = "Registrar placar, eventos e classificado da partida")
    public ResponseEntity<CampeonatoResponseCompletoDTO> registrarPartida(
            @PathVariable Long id,
            @PathVariable Long partidaId,
            @RequestBody RegistrarPartidaRequestDTO request) {
        campeonatoPartidaService.registrarResultado(id, partidaId, request);
        return ResponseEntity.ok(campeonatoService.obterCompletoPorId(id));
    }

    @GetMapping("/{id}/estatisticas")
    @Operation(summary = "Obter estatísticas do campeonato (classificação, artilharia, assistências e cartões)")
    public ResponseEntity<CampeonatoEstatisticasDTO> obterEstatisticas(@PathVariable Long id) {
        return ResponseEntity.ok(campeonatoPartidaService.obterEstatisticas(id));
    }

    @GetMapping("/{id}/remanejamento")
    @Operation(summary = "Obter informações para remanejamento de confrontos assíncronos")
    public ResponseEntity<RemanejamentoInfoResponseDTO> obterRemanejamento(@PathVariable Long id) {
        return ResponseEntity.ok(campeonatoMotorService.obterInfoRemanejamento(id));
    }

    @PostMapping("/{id}/remanejamento")
    @Operation(summary = "Confirmar remanejamento e gerar a próxima rodada")
    public ResponseEntity<CampeonatoResponseCompletoDTO> confirmarRemanejamento(
            @PathVariable Long id,
            @RequestBody RemanejamentoRequestDTO request) {
        campeonatoMotorService.confirmarRemanejamento(id, request);
        return ResponseEntity.ok(campeonatoService.obterCompletoPorId(id));
    }

    @PostMapping("/{id}/escolher-campeao")
    @Operation(summary = "Escolher manualmente o clube campeão após eliminação total na primeira rodada")
    public ResponseEntity<CampeonatoResponseCompletoDTO> escolherCampeao(
            @PathVariable Long id,
            @RequestBody DefinirVencedorRequestDTO request) {
        campeonatoMotorService.escolherCampeao(id, request);
        return ResponseEntity.ok(campeonatoService.obterCompletoPorId(id));
    }
}
