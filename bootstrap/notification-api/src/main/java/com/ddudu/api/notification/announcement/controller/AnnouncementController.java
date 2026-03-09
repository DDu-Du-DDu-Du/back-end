package com.ddudu.api.notification.announcement.controller;

import com.ddudu.api.notification.announcement.doc.AnnouncementControllerDoc;
import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.notification.SimpleAnnouncementDto;
import com.ddudu.application.common.dto.notification.request.AnnouncementSearchRequest;
import com.ddudu.application.common.dto.notification.request.CreateAnnouncementRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.notification.in.AnnouncementSearchUseCase;
import com.ddudu.application.common.port.notification.in.CreateAnnouncementUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController implements AnnouncementControllerDoc {

  private final CreateAnnouncementUseCase createAnnouncementUseCase;
  private final AnnouncementSearchUseCase announcementSearchUseCase;

  @Override
  @GetMapping
  public ResponseEntity<ScrollResponse<SimpleAnnouncementDto>> getList(
      AnnouncementSearchRequest request
  ) {
    ScrollResponse<SimpleAnnouncementDto> response = announcementSearchUseCase.search(request);

    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateAnnouncementRequest request
  ) {
    IdResponse response = createAnnouncementUseCase.create(loginId, request);

    return ResponseEntity.created(null)
        .body(response);
  }

}
