package com.ddudu.api.user.auth.config;

import com.ddudu.bootstrap.common.exception.ErrorResponse;
import com.ddudu.common.exception.AuthErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component("bearerTokenAuthenticationEntryPointWrapper")
@RequiredArgsConstructor
public class BearerTokenAuthenticationEntryPointWrapper implements AuthenticationEntryPoint {

  private final BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint =
      new BearerTokenAuthenticationEntryPoint();
  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) throws IOException, ServletException {
    this.bearerTokenAuthenticationEntryPoint.commence(request, response, authException);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    ErrorResponse errorResponse = ErrorResponse.from(AuthErrorCode.INVALID_TOKEN_AUTHORITY);
    String responseBody = objectMapper.writeValueAsString(errorResponse);

    response.getWriter()
        .write(responseBody);
  }

}
