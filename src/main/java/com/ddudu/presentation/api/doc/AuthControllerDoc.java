package com.ddudu.presentation.api.doc;

import com.ddudu.application.domain.user.domain.enums.ProviderType;
import com.ddudu.application.dto.authentication.request.TokenRefreshRequest;
import com.ddudu.application.dto.authentication.response.TokenResponse;
import com.ddudu.presentation.api.doc.error.AuthErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthControllerDoc {

  @Operation(summary = "소셜 로그인")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
      }
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

  @Operation(summary = "토큰 갱신", description = "토큰 재발급을 위한 API")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
          @ApiResponse(
              responseCode = "401", description = "UNAUTHORIZED",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "5007",
                          description = "저장된 토큰이 없거나 이미 사용한 토큰인 경우",
                          value = AuthErrorExamples.AUTH_REFRESH_NOT_ALLOWED
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "404", description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "5008",
                          description = "토큰의 사용자가 존재하지 않는 경우",
                          value = AuthErrorExamples.AUTH_USER_NOT_FOUND
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<TokenResponse> refresh(TokenRefreshRequest request);

}
