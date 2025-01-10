package mg.itu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Backend Spring Boot API Docs")
                        .description("Docs for each API End-Point")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Ny Haritina")
                                .email("nyharitinarabemanantsoa10@gmail.com")));
    }
}