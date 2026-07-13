package com.dozycoffee.infrastructure.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerWebMvcConfig {

    private static final String BEARER_SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI dozyCoffeeOpenApi() {
        return new OpenAPI()
                .info(new Info().title("Dozy Coffee Menu System API").version("v1"))
                .components(new Components().addSecuritySchemes(
                        BEARER_SECURITY_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("session-id")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SECURITY_SCHEME));
    }
}
