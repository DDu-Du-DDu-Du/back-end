package com.ddudu.presentation.api.controller;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.repeat_ddudu.util.DayOfWeekUtil;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.ddudu.request.ChangeNameRequest;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.dto.ddudu.request.MoveDateRequest;
import com.ddudu.application.dto.ddudu.request.PeriodSetupRequest;
import com.ddudu.application.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.dto.ddudu.response.DduduDetailResponse;
import com.ddudu.application.dto.ddudu.response.RepeatAnotherDayResponse;
import com.ddudu.application.dto.ddudu.response.TimetableResponse;
import com.ddudu.application.dto.scroll.response.ScrollResponse;
import com.ddudu.application.port.in.ddudu.CalculateCompletionUseCase;
import com.ddudu.application.port.in.ddudu.ChangeNameUseCase;
import com.ddudu.application.port.in.ddudu.CreateDduduUseCase;
import com.ddudu.application.port.in.ddudu.DduduSearchUseCase;
import com.ddudu.application.port.in.ddudu.DeleteDduduUseCase;
import com.ddudu.application.port.in.ddudu.GetDailyDdudusByGoalUseCase;
import com.ddudu.application.port.in.ddudu.GetTimetableUseCase;
import com.ddudu.application.port.in.ddudu.MoveDateUseCase;
import com.ddudu.application.port.in.ddudu.PeriodSetupUseCase;
import com.ddudu.application.port.in.ddudu.RepeatUseCase;
import com.ddudu.application.port.in.ddudu.RetrieveDduduUseCase;
import com.ddudu.application.port.in.ddudu.SwitchStatusUseCase;
import com.ddudu.old.todo.service.TodoService;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import com.ddudu.presentation.api.doc.DduduControllerDoc;
import jakarta.validation.Valid;
import java.net.URI;
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
@RequestMapping("/api/ddudus")
@RequiredArgsConstructor
public class DduduController implements DduduControllerDoc {

  private final CreateDduduUseCase createDduduUseCase;
  private final PeriodSetupUseCase periodSetupUseCase;
  private final GetDailyDdudusByGoalUseCase getDailyDdudusByGoalUseCase;
  private final GetTimetableUseCase getTimetableUseCase;
  private final MoveDateUseCase moveDateUseCase;
  private final RepeatUseCase repeatUseCase;
  private final DduduSearchUseCase dduduSearchUseCase;
  private final SwitchStatusUseCase switchStatusUseCase;
  private final ChangeNameUseCase changeNameUseCase;
  private final DeleteDduduUseCase deleteDduduUseCase;
  private final CalculateCompletionUseCase calculateCompletionUseCase;
  private final RetrieveDduduUseCase retrieveDduduUseCase;
  private final TodoService todoService;

  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateDduduRequest request
  ) {
    BasicDduduResponse response = createDduduUseCase.create(loginId, request);
    URI uri = URI.create("/api/ddudus/" + response.id());

    return ResponseEntity.created(uri)
        .body(new IdResponse(response.id()));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ScrollResponse<SimpleDduduSearchDto>> getList(
      @Login
      Long loginId,
      DduduSearchRequest request
  ) {
    ScrollResponse<SimpleDduduSearchDto> response = dduduSearchUseCase.search(loginId, request);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DduduDetailResponse> getById(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    DduduDetailResponse response = retrieveDduduUseCase.findById(loginId, id);

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

  @GetMapping("/completion/weekly")
  public ResponseEntity<List<DduduCompletionResponse>> getWeeklyCompletion(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate date
  ) {
    userId = isNull(userId) ? loginId : userId;
    LocalDate firstDayOfWeek = DayOfWeekUtil.getFirstDayOfWeek(date);
    LocalDate afterOneWeek = firstDayOfWeek.plusDays(7);

    List<DduduCompletionResponse> response = calculateCompletionUseCase.calculate(
        loginId, userId, firstDayOfWeek, afterOneWeek);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/monthly")
  public ResponseEntity<List<DduduCompletionResponse>> getMonthlyCompletion(
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
    userId = isNull(userId) ? loginId : userId;
    LocalDate firstDayOfMonth = isNull(yearMonth) ? YearMonth.now()
        .atDay(1) : yearMonth.atDay(1);
    LocalDate afterOneMonth = firstDayOfMonth.plusMonths(1);

    List<DduduCompletionResponse> response = calculateCompletionUseCase.calculate(
        loginId, userId, firstDayOfMonth, afterOneMonth);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<IdResponse> changeName(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      ChangeNameRequest request
  ) {
    BasicDduduResponse response = changeNameUseCase.change(loginId, id, request);
    return ResponseEntity.ok(new IdResponse(response.id()));
  }

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

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    deleteDduduUseCase.delete(loginId, id);

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
      @Valid
      MoveDateRequest request
  ) {
    moveDateUseCase.moveDate(loginId, id, request);

    return ResponseEntity.noContent()
        .build();
  }

  @PostMapping("/{id}/repeat")
  public ResponseEntity<RepeatAnotherDayResponse> repeatOnAnotherDay(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      RepeatAnotherDayRequest request
  ) {
    RepeatAnotherDayResponse response = repeatUseCase.repeatOnAnotherDay(
        loginId, id, request);
    URI uri = URI.create("/api/ddudus/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

}
