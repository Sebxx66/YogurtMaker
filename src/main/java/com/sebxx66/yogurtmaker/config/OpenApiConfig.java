package com.sebxx66.yogurtmaker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI yogurtMakerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Yogurt Maker API")
                .version("1.0.0")
                .description("Gestión de recetas, lotes de producción y control de temperatura "
                        + "para la fabricación de yogurt artesanal.")
                .contact(new Contact().name("Jhoan Sebastián Palacio Zapata").url("https://github.com/Sebxx66"))
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
    }
}
