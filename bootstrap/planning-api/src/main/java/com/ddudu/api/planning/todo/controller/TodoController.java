package com.ddudu.api.planning.todo.controller;

import static java.util.Objects.isNull;

import com.ddudu.api.planning.todo.doc.TodoControllerDoc;
import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.dto.todo.GoalGroupedTodos;
import com.ddudu.application.common.dto.todo.SimpleTodoSearchDto;
import com.ddudu.application.common.dto.todo.request.ChangeNameRequest;
import com.ddudu.application.common.dto.todo.request.CreateTodoRequest;
import com.ddudu.application.common.dto.todo.request.MoveDateRequest;
import com.ddudu.application.common.dto.todo.request.PeriodSetupRequest;
import com.ddudu.application.common.dto.todo.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.todo.request.SetReminderRequest;
import com.ddudu.application.common.dto.todo.request.TodoSearchRequest;
import com.ddudu.application.common.dto.todo.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;
import com.ddudu.application.common.dto.todo.response.RepeatAnotherDayResponse;
import com.ddudu.application.common.dto.todo.response.TimetableResponse;
import com.ddudu.application.common.dto.todo.response.TodoDetailResponse;
import com.ddudu.application.common.port.todo.in.CancelReminderUseCase;
import com.ddudu.application.common.port.todo.in.ChangeNameUseCase;
import com.ddudu.application.common.port.todo.in.CreateTodoUseCase;
import com.ddudu.application.common.port.todo.in.DeleteTodoUseCase;
import com.ddudu.application.common.port.todo.in.GetDailyTodosByGoalUseCase;
import com.ddudu.application.common.port.todo.in.GetTimetableUseCase;
import com.ddudu.application.common.port.todo.in.MoveDateUseCase;
import com.ddudu.application.common.port.todo.in.PeriodSetupUseCase;
import com.ddudu.application.common.port.todo.in.RepeatUseCase;
import com.ddudu.application.common.port.todo.in.RetrieveTodoUseCase;
import com.ddudu.application.common.port.todo.in.SetReminderUseCase;
import com.ddudu.application.common.port.todo.in.SwitchStatusUseCase;
import com.ddudu.application.common.port.todo.in.TodoSearchUseCase;
import com.ddudu.application.common.port.todo.in.UpdateTodoUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
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
public class TodoController implements TodoControllerDoc {

  private final CreateTodoUseCase createTodoUseCase;
  private final GetDailyTodosByGoalUseCase getDailyTodosByGoalUseCase;
  private final GetTimetableUseCase getTimetableUseCase;
  private final TodoSearchUseCase todoSearchUseCase;
  private final RetrieveTodoUseCase retrieveTodoUseCase;
  private final PeriodSetupUseCase periodSetupUseCase;
  private final MoveDateUseCase moveDateUseCase;
  private final RepeatUseCase repeatUseCase;
  private final SwitchStatusUseCase switchStatusUseCase;
  private final ChangeNameUseCase changeNameUseCase;
  private final UpdateTodoUseCase updateTodoUseCase;
  private final DeleteTodoUseCase deleteTodoUseCase;
  private final SetReminderUseCase setReminderUseCase;
  private final CancelReminderUseCase cancelReminderUseCase;

  /**
   * 뚜두 생성 API
   */
  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateTodoRequest request
  ) {
    BasicTodoResponse response = createTodoUseCase.create(loginId, request);
    URI uri = URI.create("/api/todos/" + response.id());

    return ResponseEntity.created(uri)
        .body(new IdResponse(response.id()));
  }

  /**
   * 일별 뚜두 리스트 조회 API (목표별로 그룹화)
   */
  @GetMapping("/daily/list")
  public ResponseEntity<List<GoalGroupedTodos>> getDailyList(
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

    List<GoalGroupedTodos> response = getDailyTodosByGoalUseCase.get(loginId, userId, date);
    return ResponseEntity.ok(response);
  }

  /**
   * 일별 시간표 조회 API
   */
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

    TimetableResponse response = getTimetableUseCase.get(loginId, userId, date);
    return ResponseEntity.ok(response);
  }

  /**
   * 뚜두 검색 API
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ScrollResponse<SimpleTodoSearchDto>> getList(
      @Login
      Long loginId,
      TodoSearchRequest request
  ) {
    ScrollResponse<SimpleTodoSearchDto> response = todoSearchUseCase.search(loginId, request);

    return ResponseEntity.ok(response);
  }

  /**
   * 뚜두 상세 조회 API
   */
  @Override
  @GetMapping("/{id}")
  public ResponseEntity<TodoDetailResponse> getById(
      @Login
      Long loginId,
      @PathVariable("id")
      Long id
  ) {
    TodoDetailResponse response = retrieveTodoUseCase.findById(loginId, id);

    return ResponseEntity.ok(response);
  }

  @Override
  @PatchMapping("/{id}/reminder")
  public ResponseEntity<Void> setReminder(
      @Login
      Long loginId,
      @PathVariable("id")
      Long id,
      @RequestBody
      @Valid
      SetReminderRequest request
  ) {
    setReminderUseCase.setReminder(loginId, id, request);

    return ResponseEntity.noContent()
        .build();
  }

  @Override
  @DeleteMapping("/{id}/reminder")
  public ResponseEntity<Void> cancelReminder(
      @Login
      Long loginId,
      @PathVariable("id")
      Long id
  ) {
    cancelReminderUseCase.cancel(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

  /**
   * 뚜두 기간(시작 시간, 종료 시간) 설정 API
   */
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

  /**
   * 뚜두명 변경 API
   */
  @PutMapping("/{id}/name")
  public ResponseEntity<IdResponse> changeName(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      ChangeNameRequest request
  ) {
    BasicTodoResponse response = changeNameUseCase.change(loginId, id, request);
    return ResponseEntity.ok(new IdResponse(response.id()));
  }

  /**
   * 뚜두 수정 API
   */
  @PutMapping("/{id}")
  public ResponseEntity<IdResponse> update(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateTodoRequest request
  ) {
    BasicTodoResponse response = updateTodoUseCase.update(loginId, id, request);
    return ResponseEntity.ok(new IdResponse(response.id()));
  }

  /**
   * 뚜두 날짜 변경 API
   */
  @PutMapping("/{id}/date")
  public ResponseEntity<Void> moveDate(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      MoveDateRequest request
  ) {
    moveDateUseCase.moveDate(loginId, id, request);

    return ResponseEntity.noContent()
        .build();
  }

  /**
   * 뚜두 상태 변경 API (진행 중 or 완료)
   */
  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> updateStatus(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    switchStatusUseCase.switchStatus(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

  /**
   * 다른 날 반복하기 API
   */
  @PostMapping("/{id}/repeat")
  public ResponseEntity<RepeatAnotherDayResponse> repeatOnAnotherDay(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      RepeatAnotherDayRequest request
  ) {
    RepeatAnotherDayResponse response = repeatUseCase.repeatOnAnotherDay(loginId, id, request);
    URI uri = URI.create("/api/todos/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  /**
   * 뚜두 삭제 API
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    deleteTodoUseCase.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

}
