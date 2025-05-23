package com.ddudu.bootstrap.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    servers = @Server(url = "/"),
    info = @Info(title = "뚜두뚜두 API"),
    security = @SecurityRequirement(name = "카카오 토큰 로그인")
)
@SecuritySchemes(
    {
        @SecurityScheme(
            name = "카카오 토큰 로그인",
            type = SecuritySchemeType.HTTP,
            bearerFormat = "JWT",
            scheme = "bearer"
        ),
        @SecurityScheme(
            name = "카카오에서 토큰 받아오기",
            type = SecuritySchemeType.OAUTH2,
            flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                    authorizationUrl = "https://kauth.kakao.com/oauth/authorize",
                    tokenUrl = "https://kauth.kakao.com/oauth/token"
                )
            )
        )
    }
)
public class SwaggerConfig {

}
