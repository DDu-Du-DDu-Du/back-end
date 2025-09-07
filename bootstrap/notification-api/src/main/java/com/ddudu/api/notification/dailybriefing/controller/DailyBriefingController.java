package com.ddudu.api.notification.dailybriefing.controller;

import com.ddudu.api.notification.dailybriefing.doc.DailyBriefingControllerDoc;
import com.ddudu.application.common.dto.notification.response.DailyBriefingResponse;
import com.ddudu.application.common.port.notification.in.BriefTodayPlanningUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/briefings")
@RequiredArgsConstructor
public class DailyBriefingController implements DailyBriefingControllerDoc {

  private final BriefTodayPlanningUseCase briefTodayPlanningUseCase;

  @Override
  @PostMapping
  public ResponseEntity<DailyBriefingResponse> getDailyBriefing(@Login Long loginId) {
    DailyBriefingResponse response = briefTodayPlanningUseCase.getDailyBriefing(loginId);

    return ResponseEntity.created(null)
        .body(response);
  }

}
