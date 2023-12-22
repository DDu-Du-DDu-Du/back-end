package com.ddudu.goal.service;

import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class GoalService {

  @Transactional
  public CreateGoalResponse create(@Valid CreateGoalRequest request) {
    // TODO: 목표 생성 및 응답 값 반환

    return null;
  }

}
