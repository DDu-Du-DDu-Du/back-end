package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import com.ddudu.application.port.in.RetrieveGoalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveGoalService implements RetrieveGoalUseCase {

  @Override
  public GoalResponse getById(Long userId, Long id) {
    // TODO: 사용자 조회
    // TODO: 목표 조회
    // TODO: 권한 검사
    // TODO: 응답 생성
    return null;
  }

}
