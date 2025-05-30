package com.ddudu.api.planning.goal.controller;

import com.ddudu.api.planning.goal.doc.GoalControllerDoc;
import com.ddudu.application.common.dto.goal.request.ChangeGoalStatusRequest;
import com.ddudu.application.common.dto.goal.request.CreateGoalRequest;
import com.ddudu.application.common.dto.goal.request.UpdateGoalRequest;
import com.ddudu.application.common.dto.goal.response.BasicGoalResponse;
import com.ddudu.application.common.dto.goal.response.GoalIdResponse;
import com.ddudu.application.common.dto.goal.response.GoalWithRepeatDduduResponse;
import com.ddudu.application.common.port.goal.in.ChangeGoalStatusUseCase;
import com.ddudu.application.common.port.goal.in.CreateGoalUseCase;
import com.ddudu.application.common.port.goal.in.DeleteGoalUseCase;
import com.ddudu.application.common.port.goal.in.RetrieveAllGoalsUseCase;
import com.ddudu.application.common.port.goal.in.RetrieveGoalUseCase;
import com.ddudu.application.common.port.goal.in.UpdateGoalUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController implements GoalControllerDoc {

  private static final String GOALS_BASE_PATH = "/api/goals/";

  private final CreateGoalUseCase createGoalUseCase;
  private final RetrieveAllGoalsUseCase retrieveAllGoalsUseCase;
  private final RetrieveGoalUseCase retrieveGoalUseCase;
  private final UpdateGoalUseCase updateGoalUseCase;
  private final ChangeGoalStatusUseCase changeGoalStatusUseCase;
  private final DeleteGoalUseCase deleteGoalUseCase;

  /**
   * 목표 생성 API (반복 뚜두도 함께 생성 가능)
   */
  @PostMapping
  public ResponseEntity<GoalIdResponse> create(
      @Login
      Long userId,
      @RequestBody
      @Valid
      CreateGoalRequest request
  ) {
    GoalIdResponse response = createGoalUseCase.create(userId, request);
    URI uri = URI.create(GOALS_BASE_PATH + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  /**
   * 목표 상세 조회 API (반복 뚜두도 함께)
   */
  @GetMapping("/{id}")
  public ResponseEntity<GoalWithRepeatDduduResponse> getById(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    GoalWithRepeatDduduResponse response = retrieveGoalUseCase.getById(loginId, id);

    return ResponseEntity.ok(response);
  }

  /**
   * 목표 전체 조회 API (사용자 기준, 뚜두도 함께)
   */
  @GetMapping
  public ResponseEntity<List<BasicGoalResponse>> getAllByUser(
      @Login
      Long loginId,
      @RequestParam
      Long userId
  ) {
    // TODO: privacy type 별 허용 목표로 조정
    List<BasicGoalResponse> response = retrieveAllGoalsUseCase.findAllByUser(userId);

    return ResponseEntity.ok(response);
  }

  /**
   * 목표 수정 API (수정 가능: 목표명, 색상, 공개 범위)
   */
  @PutMapping("/{id}")
  public ResponseEntity<GoalIdResponse> update(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateGoalRequest request
  ) {
    GoalIdResponse response = updateGoalUseCase.update(loginId, id, request);

    return ResponseEntity.ok()
        .body(response);
  }

  /**
   * 목표 상태 변경 API (진행 중 or 완료)
   */
  @PatchMapping("/{id}")
  public ResponseEntity<GoalIdResponse> changeStatus(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      ChangeGoalStatusRequest request
  ) {
    GoalIdResponse response = changeGoalStatusUseCase.changeStatus(loginId, id, request);

    return ResponseEntity.ok()
        .body(response);
  }

  /**
   * 목표 삭제 API (해당 목표의 반복 뚜두 / 뚜두도 함께 삭제)
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Login
      @Parameter(hidden = true)
      Long loginId,
      @PathVariable
      Long id
  ) {
    deleteGoalUseCase.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

}
