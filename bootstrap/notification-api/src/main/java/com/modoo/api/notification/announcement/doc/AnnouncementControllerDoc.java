package com.modoo.api.notification.announcement.doc;

import com.modoo.application.common.dto.IdResponse;
import com.modoo.application.common.dto.notification.SimpleAnnouncementDto;
import com.modoo.application.common.dto.notification.request.AnnouncementSearchRequest;
import com.modoo.application.common.dto.notification.request.CreateAnnouncementRequest;
import com.modoo.application.common.dto.notification.request.UpdateAnnouncementRequest;
import com.modoo.application.common.dto.notification.response.AnnouncementDetailResponse;
import com.modoo.application.common.dto.scroll.response.ScrollResponse;
import com.modoo.bootstrap.common.doc.examples.AnnouncementErrorExamples;
import com.modoo.bootstrap.common.doc.examples.AuthErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "공지사항",
    description = "공지사항 API"
)
public interface AnnouncementControllerDoc {

  @Operation(
      summary = "공지사항 목록 조회",
      description = "공지사항을 커서 기반 페이지네이션으로 조회합니다."
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

  @Operation(summary = "공지사항 상세 조회")
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "13008",
                      value = AnnouncementErrorExamples.ANNOUNCEMENT_NOT_EXISTING
                  )
              )
          )
      }
  )
  ResponseEntity<AnnouncementDetailResponse> getById(
      @Parameter(
          name = "id",
          required = true,
          description = "조회할 공지사항 ID",
          in = ParameterIn.PATH
      )
      Long id
  );

  @Operation(summary = "공지사항 수정")
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
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
                  examples = {
                      @ExampleObject(
                          name = "13006",
                          value = AnnouncementErrorExamples.LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "13008",
                          value = AnnouncementErrorExamples.ANNOUNCEMENT_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<IdResponse> update(
      Long loginId,
      @Parameter(
          name = "id",
          required = true,
          description = "수정할 공지사항 ID",
          in = ParameterIn.PATH
      )
      Long id,
      UpdateAnnouncementRequest request
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
