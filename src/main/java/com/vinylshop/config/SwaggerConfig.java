package com.vinylshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vinyl Store API")
                        .description("Документація для бекенду інтернет-магазину вінілових платівок")
                        .version("1.0.0"));
    }
}

