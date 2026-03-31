package com.ddudu.api.planning.reminder.doc;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.reminder.request.CreateReminderRequest;
import com.ddudu.application.common.dto.reminder.request.UpdateReminderRequest;
import com.ddudu.bootstrap.common.doc.examples.AuthErrorExamples;
import com.ddudu.bootstrap.common.doc.examples.ReminderErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Reminder",
    description = "미리알림 관련 API"
)
public interface ReminderControllerDoc {

  @Operation(summary = "미리알림 생성")
  @ApiResponses(
      value = {
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
                          name = "2102",
                          value = ReminderErrorExamples.REMINDER_NULL_TODO_VALUE
                      ),
                      @ExampleObject(
                          name = "2103",
                          value = ReminderErrorExamples.REMINDER_NULL_REMINDS_AT
                      ),
                      @ExampleObject(
                          name = "2104",
                          value = ReminderErrorExamples.REMINDER_NULL_SCHEDULED_AT
                      ),
                      @ExampleObject(
                          name = "2105",
                          value = ReminderErrorExamples.REMINDER_INVALID_REMINDS_AT
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
                      name = "2108",
                      value = ReminderErrorExamples.REMINDER_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2106",
                          value = ReminderErrorExamples.REMINDER_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2107",
                          value = ReminderErrorExamples.REMINDER_TODO_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<IdResponse> create(Long loginId, CreateReminderRequest request);

  @Operation(summary = "미리알림 갱신")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "NO_CONTENT"
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2103",
                          value = ReminderErrorExamples.REMINDER_NULL_REMINDS_AT
                      ),
                      @ExampleObject(
                          name = "2104",
                          value = ReminderErrorExamples.REMINDER_NULL_SCHEDULED_AT
                      ),
                      @ExampleObject(
                          name = "2105",
                          value = ReminderErrorExamples.REMINDER_INVALID_REMINDS_AT
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
                      name = "2108",
                      value = ReminderErrorExamples.REMINDER_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "2106",
                          value = ReminderErrorExamples.REMINDER_LOGIN_USER_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2107",
                          value = ReminderErrorExamples.REMINDER_TODO_NOT_EXISTING
                      ),
                      @ExampleObject(
                          name = "2111",
                          value = ReminderErrorExamples.REMINDER_ID_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<Void> update(Long loginId, Long id, UpdateReminderRequest request);

}
