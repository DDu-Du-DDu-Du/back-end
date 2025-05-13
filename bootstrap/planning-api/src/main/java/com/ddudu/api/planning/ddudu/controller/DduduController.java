package com.ddudu.api.planning.ddudu.controller;

import static java.util.Objects.isNull;

import com.ddudu.api.planning.ddudu.doc.DduduControllerDoc;
import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.common.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.ddudu.request.ChangeNameRequest;
import com.ddudu.application.common.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.common.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.common.dto.ddudu.request.MoveDateRequest;
import com.ddudu.application.common.dto.ddudu.request.PeriodSetupRequest;
import com.ddudu.application.common.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.common.dto.ddudu.response.DduduDetailResponse;
import com.ddudu.application.common.dto.ddudu.response.RepeatAnotherDayResponse;
import com.ddudu.application.common.dto.ddudu.response.TimetableResponse;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.ddudu.in.ChangeNameUseCase;
import com.ddudu.application.common.port.ddudu.in.CreateDduduUseCase;
import com.ddudu.application.common.port.ddudu.in.DduduSearchUseCase;
import com.ddudu.application.common.port.ddudu.in.DeleteDduduUseCase;
import com.ddudu.application.common.port.ddudu.in.GetDailyDdudusByGoalUseCase;
import com.ddudu.application.common.port.ddudu.in.GetTimetableUseCase;
import com.ddudu.application.common.port.ddudu.in.MoveDateUseCase;
import com.ddudu.application.common.port.ddudu.in.PeriodSetupUseCase;
import com.ddudu.application.common.port.ddudu.in.RepeatUseCase;
import com.ddudu.application.common.port.ddudu.in.RetrieveDduduUseCase;
import com.ddudu.application.common.port.ddudu.in.SwitchStatusUseCase;
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
@RequestMapping("/api/ddudus")
@RequiredArgsConstructor
public class DduduController implements DduduControllerDoc {

  private final CreateDduduUseCase createDduduUseCase;
  private final GetDailyDdudusByGoalUseCase getDailyDdudusByGoalUseCase;
  private final GetTimetableUseCase getTimetableUseCase;
  private final DduduSearchUseCase dduduSearchUseCase;
  private final RetrieveDduduUseCase retrieveDduduUseCase;
  private final PeriodSetupUseCase periodSetupUseCase;
  private final MoveDateUseCase moveDateUseCase;
  private final RepeatUseCase repeatUseCase;
  private final SwitchStatusUseCase switchStatusUseCase;
  private final ChangeNameUseCase changeNameUseCase;
  private final DeleteDduduUseCase deleteDduduUseCase;

  /**
   * 뚜두 생성 API
   */
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

  /**
   * 일별 뚜두 리스트 조회 API (목표별로 그룹화)
   */
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

    List<GoalGroupedDdudus> response = getDailyDdudusByGoalUseCase.get(loginId, userId, date);
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
  public ResponseEntity<ScrollResponse<SimpleDduduSearchDto>> getList(
      @Login
      Long loginId,
      DduduSearchRequest request
  ) {
    ScrollResponse<SimpleDduduSearchDto> response = dduduSearchUseCase.search(loginId, request);

    return ResponseEntity.ok(response);
  }

  /**
   * 뚜두 상세 조회 API
   */
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
    URI uri = URI.create("/api/ddudus/" + response.id());

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
    deleteDduduUseCase.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

}
