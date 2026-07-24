package br.com.bellato.gerenciador_fifa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.auth.UserResponseDTO;
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

    @GetMapping("/{id}")
    @Operation(summary = "Obter usuário por ID")
    public ResponseEntity<UserResponseDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obterPorId(id));
    }
}
