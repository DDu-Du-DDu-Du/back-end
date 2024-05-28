package com.ddudu.presentation.api.controller;

import static java.util.Objects.isNull;

import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.request.MoveDateRequest;
import com.ddudu.application.dto.ddudu.request.PeriodSetupRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.dto.ddudu.response.TimetableResponse;
import com.ddudu.application.port.in.ddudu.GetDailyDdudusByGoalUseCase;
import com.ddudu.application.port.in.ddudu.GetTimetableUseCase;
import com.ddudu.application.port.in.ddudu.PeriodSetupUseCase;
import com.ddudu.old.todo.dto.request.CreateTodoRequest;
import com.ddudu.old.todo.dto.request.UpdateTodoRequest;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.todo.dto.response.TodoResponse;
import com.ddudu.old.todo.service.TodoService;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.doc.DduduControllerDoc;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/ddudus")
@RequiredArgsConstructor
public class DduduController implements DduduControllerDoc {

  private final PeriodSetupUseCase periodSetupUseCase;
  private final GetDailyDdudusByGoalUseCase getDailyDdudusByGoalUseCase;
  private final GetTimetableUseCase getTimetableUseCase;
  private final TodoService todoService;

  @PostMapping
  @Deprecated
  public ResponseEntity<BasicDduduResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateTodoRequest request
  ) {
    BasicDduduResponse response = todoService.create(loginId, request);
    URI uri = URI.create("/api/ddudus/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @GetMapping("/{id}")
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

  @GetMapping("/daily/list")
  public ResponseEntity<List<GoalGroupedDdudus>> getDailyList(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate date
  ) {
    userId = isNull(userId) ? loginId : userId;
    date = isNull(date) ? LocalDate.now() : date;

    List<GoalGroupedDdudus> response = getDailyDdudusByGoalUseCase.get(
        loginId, userId, date);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/daily/timetable")
  public ResponseEntity<TimetableResponse> getDailyTimetable(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate date
  ) {
    userId = isNull(userId) ? loginId : userId;
    date = isNull(date) ? LocalDate.now() : date;

    TimetableResponse response = getTimetableUseCase.get(
        loginId, userId, date);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/weekly")
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

  @GetMapping("/monthly")
  @Deprecated
  public ResponseEntity<List<TodoCompletionResponse>> getMonthlyCompletion(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(
          value = "date",
          required = false
      )
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
  @Deprecated
  public ResponseEntity<BasicDduduResponse> update(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateTodoRequest request
  ) {
    BasicDduduResponse response = todoService.update(loginId, id, request);

    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/status")
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

  @PutMapping("/{id}/period")
  public ResponseEntity<Void> setUpPeriod(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      PeriodSetupRequest request
  ) {
    periodSetupUseCase.setUpPeriod(loginId, id, request);

    return ResponseEntity.noContent()
        .build();
  }

  @PutMapping("/{id}/date")
  public ResponseEntity<Void> moveDate(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      MoveDateRequest request
  ) {
    return null;
  }

}
