package com.ddudu.presentation.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    servers = @Server(url = "/"),
    info = @Info(
        title = "뚜두뚜두 API"
    )
)
public class SwaggerConfig {

}
