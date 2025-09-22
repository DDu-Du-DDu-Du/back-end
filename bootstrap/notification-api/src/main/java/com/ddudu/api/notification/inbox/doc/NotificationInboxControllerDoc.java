package com.ddudu.api.notification.inbox.doc;

import com.ddudu.application.common.dto.notification.request.NotificationInboxSearchRequest;
import com.ddudu.application.common.dto.notification.response.NotificationInboxSearchResponse;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.NotificationInboxErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Notification Inbox",
    description = "알림 인박스 관련 API"
)
public interface NotificationInboxControllerDoc {

  @Operation(
      summary = "알림 인박스 목록 조회",
      description = "로그인 사용자의 알림 인박스를 커서 기반으로 스크롤 조회합니다."
  )
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
                      name = "11007",
                      value = NotificationInboxErrorExamples.LOGIN_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  ResponseEntity<ScrollResponse<NotificationInboxSearchResponse>> getList(
      Long loginId,
      @ParameterObject
      NotificationInboxSearchRequest request
  );

}
