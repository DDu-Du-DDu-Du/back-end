package com.ddudu.api.notification.announcement.doc;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.notification.SimpleAnnouncementDto;
import com.ddudu.application.common.dto.notification.request.AnnouncementSearchRequest;
import com.ddudu.application.common.dto.notification.request.CreateAnnouncementRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.bootstrap.common.doc.examples.AnnouncementErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Announcement",
    description = "공지사항 API"
)
public interface AnnouncementControllerDoc {

  @Operation(
      summary = "공지사항 목록 조회",
      description = "공지사항 목록을 커서 기반으로 스크롤 조회합니다."
  )
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          )
      }
  )
  ResponseEntity<ScrollResponse<SimpleAnnouncementDto>> getList(
      @ParameterObject
      AnnouncementSearchRequest request
  );

  @Operation(summary = "공지사항 등록")
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
                          name = "13001",
                          value = AnnouncementErrorExamples.NULL_TITLE
                      ),
                      @ExampleObject(
                          name = "13002",
                          value = AnnouncementErrorExamples.EXCESSIVE_TITLE_LENGTH
                      ),
                      @ExampleObject(
                          name = "13003",
                          value = AnnouncementErrorExamples.NULL_CONTENTS
                      ),
                      @ExampleObject(
                          name = "13004",
                          value = AnnouncementErrorExamples.EXCESSIVE_CONTENTS_LENGTH
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
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "13007",
                      value = AnnouncementErrorExamples.INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "13006",
                      value = AnnouncementErrorExamples.LOGIN_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  ResponseEntity<IdResponse> create(Long loginId, CreateAnnouncementRequest request);

}
