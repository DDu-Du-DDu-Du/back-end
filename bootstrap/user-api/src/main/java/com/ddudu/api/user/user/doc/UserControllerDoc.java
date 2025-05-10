package com.ddudu.api.user.user.doc;

import com.ddudu.application.common.dto.user.response.MeResponse;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.UserErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "User",
    description = "회원 관련 API"
)
public interface UserControllerDoc {

  @Operation(summary = "내 정보 조회")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "5002",
                          value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                      ),
                      @ExampleObject(
                          name = "1011",
                          description = "토큰의 사용자가 존재하지 않는 경우",
                          value = UserErrorExamples.USER_NO_TARGET_FOR_MY_INFO
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<MeResponse> validateToken(Long loginId);

}
