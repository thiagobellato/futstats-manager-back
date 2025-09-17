package br.com.bellato.gerenciador_fifa.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1️⃣ Pegando os domínios permitidos de uma variável de ambiente
        String allowedOrigins = System.getenv("APP_CORS_ALLOWED_ORIGINS");
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            // Se tiver múltiplos domínios, separar por vírgula
            config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        } else {
            // fallback: localhost + domínio do Railway
            config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://futstats-manager-back-production.up.railway.app"
            ));
        }

        // 2️⃣ Permitir todos os métodos HTTP que você vai usar
        config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));

        // 3️⃣ Permitir todos os headers que o frontend pode enviar
        config.setAllowedHeaders(Arrays.asList("*"));

        // 4️⃣ Se precisar enviar cookies ou autenticação, true; se não, pode ser false
        config.setAllowCredentials(true);

        // 5️⃣ Aplicar essa configuração para todas as rotas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
