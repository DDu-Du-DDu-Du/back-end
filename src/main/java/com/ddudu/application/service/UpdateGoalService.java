package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import com.ddudu.application.port.in.UpdateGoalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateGoalService implements UpdateGoalUseCase {

  @Override
  public GoalResponse update(Long userId, Long id, UpdateGoalRequest request) {
    // TODO: 사용자 조회
    // TODO: 목표 조회
    // TODO: 사용자가 목표의 소유자인지 확인
    // TODO: 목표 업데이트
    return null;
  }

}
