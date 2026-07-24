package br.com.bellato.gerenciador_fifa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.auth.AuthResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.auth.LoginRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.auth.RegisterRequestDTO;
import br.com.bellato.gerenciador_fifa.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Cadastrar novo usuário")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(201).body(authenticationService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter JWT")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
