package br.com.bellato.gerenciador_fifa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.auth.AuthResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.auth.LoginRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.auth.RegisterRequestDTO;
import br.com.bellato.gerenciador_fifa.mapper.auth.UserMapper;
import br.com.bellato.gerenciador_fifa.model.Competitor;
import br.com.bellato.gerenciador_fifa.model.User;
import br.com.bellato.gerenciador_fifa.repository.UserRepository;
import br.com.bellato.gerenciador_fifa.validator.AuthValidator;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        AuthValidator.validarRegistro(request.getUsername(), request.getPassword());

        String username = request.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Nome de usuário já está em uso.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        Competitor competitor = new Competitor();
        competitor.setDisplayName(username);
        competitor.setUser(user);
        user.setCompetitor(competitor);

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getUsername());

        return new AuthResponseDTO(token, UserMapper.toDTO(saved));
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        AuthValidator.validarLogin(request.getUsername(), request.getPassword());

        String username = request.getUsername().trim();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword()));

        User user = userRepository.findByUsernameComCompetitor(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário ou senha inválidos."));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Usuário desabilitado.");
        }

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponseDTO(token, UserMapper.toDTO(user));
    }
}
