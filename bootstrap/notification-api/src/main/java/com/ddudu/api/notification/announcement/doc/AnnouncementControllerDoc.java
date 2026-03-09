package com.ddudu.api.notification.announcement.doc;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.notification.request.CreateAnnouncementRequest;
import com.ddudu.application.common.dto.notification.response.AnnouncementDetailResponse;
import com.ddudu.bootstrap.common.doc.examples.AnnouncementErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Announcement",
    description = "Announcement API"
)
public interface AnnouncementControllerDoc {

  @Operation(summary = "Retrieve announcement detail")
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
          description = "Announcement id",
          in = ParameterIn.PATH
      )
      Long id
  );

  @Operation(summary = "Create announcement")
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
