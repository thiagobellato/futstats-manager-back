package br.com.bellato.gerenciador_fifa.dto.auth;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.bellato.gerenciador_fifa.dto.competitor.CompetitorResponseDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {

    private Long id;
    private String username;
    private LocalDateTime createdAt;
    private Boolean enabled;
    private CompetitorResponseDTO competitor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public CompetitorResponseDTO getCompetitor() {
        return competitor;
    }

    public void setCompetitor(CompetitorResponseDTO competitor) {
        this.competitor = competitor;
    }
}
