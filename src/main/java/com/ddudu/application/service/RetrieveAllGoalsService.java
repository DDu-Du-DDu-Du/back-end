package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.dto.response.GoalSummaryResponse;
import com.ddudu.application.port.in.RetrieveAllGoalsUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveAllGoalsService implements RetrieveAllGoalsUseCase {

  @Override
  public List<GoalSummaryResponse> findAllByUser(Long userId) {
    // TODO: 사용자 조회
    // TODO: 사용자의 목표 조회
    // TODO: DTO로 변환 및 반환

    return null;
  }

}
