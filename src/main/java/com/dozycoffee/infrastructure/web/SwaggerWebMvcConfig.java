package com.dozycoffee.infrastructure.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.configuration.SpringDocSpecPropertiesConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot 없이 springdoc-openapi를 사용하기 위한 수동 배선.
 * springdoc이 spring-boot-autoconfigure의 AutoConfiguration.imports로 등록해두는
 * 설정 클래스들을 직접 @Import한다 (Boot의 자동구성 처리기가 없어 자동으로 로드되지 않음).
 */
@Configuration
@Import({
        SpringDocConfiguration.class,
        SpringDocConfigProperties.class,
        SpringDocSpecPropertiesConfiguration.class,
        SpringDocWebMvcConfiguration.class,
        MultipleOpenApiSupportConfiguration.class,
        SwaggerConfig.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class
})
public class SwaggerWebMvcConfig {

    private static final String BEARER_SECURITY_SCHEME = "bearerAuth";

    /**
     * springdoc의 웹 리소스/리다이렉트 처리 빈들이 의존하는 Boot 프로퍼티 객체.
     * spring-boot-autoconfigure는 껐지만(=@EnableAutoConfiguration 미사용), 이 타입 자체는
     * 순수 POJO라 바인딩 없이 기본값으로 직접 등록해도 무방하다.
     */
    @Bean
    public WebProperties webProperties() {
        return new WebProperties();
    }

    @Bean
    public WebMvcProperties webMvcProperties() {
        return new WebMvcProperties();
    }

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
