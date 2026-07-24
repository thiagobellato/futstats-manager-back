package br.com.bellato.gerenciador_fifa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.auth.UserResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.user.PageResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.user.RivalidadeDetalheDTO;
import br.com.bellato.gerenciador_fifa.dto.user.RivalidadeItemDTO;
import br.com.bellato.gerenciador_fifa.dto.user.UserSearchItemDTO;
import br.com.bellato.gerenciador_fifa.dto.user.UsuarioPerfilResponseDTO;
import br.com.bellato.gerenciador_fifa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário autenticado")
    public ResponseEntity<UserResponseDTO> me() {
        return ResponseEntity.ok(userService.obterUsuarioAtual());
    }

    @GetMapping("/me/perfil")
    @Operation(summary = "Perfil estatístico do usuário autenticado")
    public ResponseEntity<UsuarioPerfilResponseDTO> meuPerfil() {
        return ResponseEntity.ok(userService.obterPerfilAtual());
    }

    @GetMapping("/me/rivalidades")
    @Operation(summary = "Rivalidades do usuário autenticado")
    public ResponseEntity<PageResponseDTO<RivalidadeItemDTO>> minhasRivalidades(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.listarRivalidadesAtual(q, page, size));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuários por username (paginado). Exclui desabilitados.")
    public ResponseEntity<PageResponseDTO<UserSearchItemDTO>> buscar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long excludeUserId) {
        return ResponseEntity.ok(userService.buscar(q, page, size, excludeUserId));
    }

    @GetMapping("/rivalidades/{rivalidadeId}")
    @Operation(summary = "Detalhe da rivalidade (head-to-head) entre dois usuários")
    public ResponseEntity<RivalidadeDetalheDTO> detalheRivalidade(@PathVariable Long rivalidadeId) {
        return ResponseEntity.ok(userService.obterRivalidade(rivalidadeId));
    }

    @GetMapping("/{id}/perfil")
    @Operation(summary = "Perfil estatístico do usuário")
    public ResponseEntity<UsuarioPerfilResponseDTO> perfil(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obterPerfil(id));
    }

    @GetMapping("/{id}/rivalidades")
    @Operation(summary = "Rivalidades do usuário (paginado)")
    public ResponseEntity<PageResponseDTO<RivalidadeItemDTO>> rivalidades(
            @PathVariable Long id,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.listarRivalidades(id, q, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter usuário por ID")
    public ResponseEntity<UserResponseDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obterPorId(id));
    }
}
