package br.com.fiap.rotaseca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI rotaSecaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Rota Seca API")
                .version("1.0.0")
                .description("CitizenTech que avisa moradores de áreas que alagam sobre chuva forte e indica "
                        + "pontos de apoio seguros. Global Solution FIAP 2026.2."));
    }

    /** Libera o front-end (servido em outro domínio) para chamar a API. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
