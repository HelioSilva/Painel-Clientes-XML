package com.computek.painel.Config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("API de Exemplo")
                    .version("1.0.0")
                    .description("Documentação da API para o sistema de gerenciamento de clientes")
            )
    }
}
