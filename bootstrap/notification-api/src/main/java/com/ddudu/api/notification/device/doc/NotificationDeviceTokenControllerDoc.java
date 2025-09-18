package com.ddudu.api.notification.device.doc;

import com.ddudu.application.common.dto.notification.request.SaveDeviceTokenRequest;
import com.ddudu.application.common.dto.notification.response.SaveDeviceTokenResponse;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.NotificationDeviceTokenErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Notification Device Token",
    description = "디바이스 토큰 관리 API"
)
public interface NotificationDeviceTokenControllerDoc {

  @Operation(summary = "디바이스 토큰 등록")
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "201",
              description = "CREATED",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "12002",
                          value = NotificationDeviceTokenErrorExamples.INVALID_CHANNEL
                      ),
                      @ExampleObject(
                          name = "12003",
                          value = NotificationDeviceTokenErrorExamples.NULL_TOKEN
                      ),
                      @ExampleObject(
                          name = "12004",
                          value = NotificationDeviceTokenErrorExamples.EXCESSIVE_TOKEN_LENGTH
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "12005",
                      value = NotificationDeviceTokenErrorExamples.LOGIN_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  ResponseEntity<SaveDeviceTokenResponse> registerDeviceToken(
      Long loginId,
      SaveDeviceTokenRequest request
  );

}
