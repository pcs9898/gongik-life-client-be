package org.example.gongiklifeclientbeauthservice.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("https://clientbe.gongik.shop"))
                .addServersItem(new Server().url("http://localhost:8001"))
                .info(new Info()
                        .title("Auth Rest API")
                        .version("1.0")
                        .description("Auth Rest API Documentation")
                        .contact(new Contact().name("pcs9898"))
                )
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )

                ;
    }
}
