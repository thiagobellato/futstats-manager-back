package br.com.bellato.gerenciador_fifa.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    /**
     * Defina no Highway / local:
     * - Variável APP_CORS_ALLOWED_ORIGINS
     * - Ex: "http://localhost:3000,https://futstats-manager-back-production.up.railway.app"
     */
    @Value("${APP_CORS_ALLOWED_ORIGINS}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir múltiplos domínios, separados por vírgula na variável
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // Permitir os métodos HTTP necessários
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permitir todos os headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Se precisar enviar cookies ou autenticação
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Aplicar a configuração em todas as rotas
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
