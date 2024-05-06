package com.ddudu.presentation.api.controller;

import com.ddudu.old.todo.dto.request.CreateTodoRequest;
import com.ddudu.old.todo.dto.request.UpdateTodoRequest;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.todo.dto.response.TodoInfo;
import com.ddudu.old.todo.dto.response.TodoListResponse;
import com.ddudu.old.todo.dto.response.TodoResponse;
import com.ddudu.old.todo.service.TodoService;
import com.ddudu.presentation.api.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "뚜두 관련 API")
public class TodoController {

  private final TodoService todoService;

  @PostMapping
  @Operation(summary = "뚜두 생성")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TodoInfo.class)
      )
  )
  @Deprecated
  public ResponseEntity<TodoInfo> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateTodoRequest request
  ) {
    TodoInfo response = todoService.create(loginId, request);
    URI uri = URI.create("/api/todos/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "뚜두 상세 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TodoResponse.class)
      )
  )
  @Deprecated
  public ResponseEntity<TodoResponse> getById(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    TodoResponse response = todoService.findById(loginId, id);

    return ResponseEntity.ok(response);
  }

  @GetMapping(
      value = "/daily",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "일간 뚜두 조회")
  @ApiResponse(
      responseCode = "200"
  )
  @Deprecated
  public ResponseEntity<List<TodoListResponse>> getDaily(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate date
  ) {
    userId = (userId == null) ? loginId : userId;
    date = (date == null) ? LocalDate.now() : date;
    List<TodoListResponse> response = todoService.findAllByDate(loginId, userId, date);

    return ResponseEntity.ok(response);
  }

  @GetMapping(
      value = "/weekly",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "주간 뚜두 조회")
  @ApiResponse(
      responseCode = "200"
  )
  @Deprecated
  public ResponseEntity<List<TodoCompletionResponse>> getWeeklyCompletion(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate date
  ) {
    userId = (userId == null) ? loginId : userId;
    DayOfWeek weekStart = DayOfWeek.MONDAY;
    date = (date == null) ? LocalDate.now()
        .with(weekStart) : date.with(weekStart);
    List<TodoCompletionResponse> completionList = todoService.findWeeklyCompletions(
        loginId, userId, date);

    return ResponseEntity.ok(completionList);
  }

  @GetMapping(
      value = "/monthly",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "월간 뚜두 조회")
  @ApiResponse(
      responseCode = "200"
  )
  @Deprecated
  public ResponseEntity<List<TodoCompletionResponse>> getMonthlyCompletion(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(value = "date", required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    userId = (userId == null) ? loginId : userId;
    yearMonth = (yearMonth == null) ? YearMonth.now() : yearMonth;
    List<TodoCompletionResponse> completionList = todoService.findMonthlyCompletions(
        loginId, userId, yearMonth);

    return ResponseEntity.ok(completionList);
  }

  @PutMapping("/{id}")
  @Operation(summary = "뚜두 수정")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TodoInfo.class)
      )
  )
  @Deprecated
  public ResponseEntity<TodoInfo> update(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateTodoRequest request
  ) {
    TodoInfo response = todoService.update(loginId, id, request);

    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/status")
  @Operation(summary = "뚜두 상태 변경")
  @ApiResponse(
      responseCode = "204"
  )
  @Deprecated
  public ResponseEntity<Void> updateStatus(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    todoService.updateStatus(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "뚜두 삭제")
  @ApiResponse(
      responseCode = "204"
  )
  @Deprecated
  public ResponseEntity<Void> delete(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    todoService.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

}
