package tech.challenge.vaccination.system.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
            .info(new Info()
                .title("Vaccination System API")
                .version("v1")
                .description("API para o sistema de gerenciamento de vacinacao.\n\n"
                    + "## Como autenticar\n"
                    + "1. Execute `POST /auth/login` com login e senha\n"
                    + "2. Copie o valor de `accessToken` da resposta\n"
                    + "3. Clique no botao **Authorize** (cadeado) acima\n"
                    + "4. Cole o token no campo Value e clique em **Authorize**\n\n"
                    + "### Credenciais de teste\n"
                    + "| Login | Senha | Role |\n"
                    + "|---|---|---|\n"
                    + "| admin | admin123 | ADMIN |\n"
                    + "| enf.carlos | enf123 | ENFERMEIRO |\n"
                    + "| enf.ana | enf123 | ENFERMEIRO |")
            )
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            );
    }

    @Bean
    public OperationCustomizer securityCustomizer() {
        return (operation, handlerMethod) -> {
            var preAuth = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            if (preAuth != null) {
                operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            }
            return operation;
        };
    }
}