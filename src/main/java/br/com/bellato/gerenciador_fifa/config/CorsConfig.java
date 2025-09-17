package br.com.bellato.gerenciador_fifa.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    /**
     * Variável de ambiente para domínios permitidos.
     * Pode conter múltiplos domínios separados por vírgula.
     * Exemplo:
     * APP_CORS_ALLOWED_ORIGINS=https://futstats-manager-back-production.up.railway.app,http://localhost:3000
     */
    @Value("${APP_CORS_ALLOWED_ORIGINS:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Transforma a string de domínios em lista
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                                     .map(String::trim)
                                     .collect(Collectors.toList());

        config.setAllowedOrigins(origins);                    // Domínios permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos HTTP
        config.setAllowedHeaders(Arrays.asList("*"));         // Todos os headers
        config.setAllowCredentials(true);                     // Permite cookies/autenticação

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);      // Aplica para todas as rotas

        return new CorsFilter(source);
    }
}
