package br.com.bellato.gerenciador_fifa.mapper.auth;

import br.com.bellato.gerenciador_fifa.dto.auth.UserResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.competitor.CompetitorResponseDTO;
import br.com.bellato.gerenciador_fifa.model.Competitor;
import br.com.bellato.gerenciador_fifa.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setEnabled(user.isEnabled());
        dto.setCompetitor(toCompetitorDTO(user.getCompetitor()));
        return dto;
    }

    public static CompetitorResponseDTO toCompetitorDTO(Competitor competitor) {
        if (competitor == null) {
            return null;
        }
        CompetitorResponseDTO dto = new CompetitorResponseDTO();
        dto.setId(competitor.getCompetitorId());
        dto.setDisplayName(competitor.getDisplayName());
        dto.setCreatedAt(competitor.getCreatedAt());
        return dto;
    }
}
