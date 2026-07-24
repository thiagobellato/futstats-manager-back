package br.com.bellato.gerenciador_fifa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.auth.UserResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.auth.UserMapper;
import br.com.bellato.gerenciador_fifa.model.User;
import br.com.bellato.gerenciador_fifa.repository.UserRepository;
import br.com.bellato.gerenciador_fifa.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponseDTO obterUsuarioAtual() {
        User user = obterEntidadeAtual();
        return UserMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO obterPorId(Long id) {
        User user = userRepository.findByIdComCompetitor(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
        return UserMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public User obterEntidadeAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return userRepository.findByIdComCompetitor(principal.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }
}
