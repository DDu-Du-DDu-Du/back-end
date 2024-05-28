package com.ddudu.presentation.api.doc;

import com.ddudu.application.domain.user.domain.enums.ProviderType;
import com.ddudu.application.dto.authentication.request.TokenRefreshRequest;
import com.ddudu.application.dto.authentication.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증 관련 API")
public interface AuthControllerDoc {

  @Operation(summary = "소셜 로그인")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TokenResponse.class)
      )
  )
  @SecurityRequirement(name = "카카오에서 토큰 받아오기")
  @Parameter(
      name = "providerType",
      in = ParameterIn.PATH,
      schema = @Schema(
          implementation = ProviderType.class,
          defaultValue = "KAKAO"
      )
  )
  ResponseEntity<TokenResponse> login(String socialToken, String providerType);

  @Operation(summary = "토큰 갱신")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TokenResponse.class)
      )
  )
  ResponseEntity<TokenResponse> refresh(TokenRefreshRequest request);

}
