package com.dobidan.bandeutseolap.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;

/**
 * Swagger(OpenAPI) 설정
 *
 * - JWT 인증을 Swagger UI에서 테스트할 수 있도록 설정한다.
 * - Swagger 상단의 "Authorize" 버튼을 통해 Bearer 토큰 입력 가능.
 */
@Configuration
public class SwaggerConfig {

    // Swagger application/octet-stream 인식 오류로 인한 컨피그 설정 변경
    public SwaggerConfig(MappingJackson2HttpMessageConverter converter) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }


    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT";

        // Bearer Token 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // API 전체에 JWT 요구하도록 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        return new OpenAPI()
                .info(new Info()
                        .title("Bandeutseolap API")
                        .description("Spring Boot + JWT + Swagger API 문서")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, bearerAuth))
                .addSecurityItem(securityRequirement);
    }
}