package com.modoo.api.notification.announcement.controller;

import com.modoo.api.notification.announcement.doc.AnnouncementControllerDoc;
import com.modoo.application.common.dto.IdResponse;
import com.modoo.application.common.dto.notification.SimpleAnnouncementDto;
import com.modoo.application.common.dto.notification.request.AnnouncementSearchRequest;
import com.modoo.application.common.dto.notification.request.CreateAnnouncementRequest;
import com.modoo.application.common.dto.notification.request.UpdateAnnouncementRequest;
import com.modoo.application.common.dto.notification.response.AnnouncementDetailResponse;
import com.modoo.application.common.dto.scroll.response.ScrollResponse;
import com.modoo.application.common.port.notification.in.AnnouncementSearchUseCase;
import com.modoo.application.common.port.notification.in.CreateAnnouncementUseCase;
import com.modoo.application.common.port.notification.in.RetrieveAnnouncementUseCase;
import com.modoo.application.common.port.notification.in.UpdateAnnouncementUseCase;
import com.modoo.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController implements AnnouncementControllerDoc {

  private final CreateAnnouncementUseCase createAnnouncementUseCase;
  private final AnnouncementSearchUseCase announcementSearchUseCase;
  private final RetrieveAnnouncementUseCase retrieveAnnouncementUseCase;
  private final UpdateAnnouncementUseCase updateAnnouncementUseCase;

  @Override
  @GetMapping
  public ResponseEntity<ScrollResponse<SimpleAnnouncementDto>> getList(
      AnnouncementSearchRequest request
  ) {
    ScrollResponse<SimpleAnnouncementDto> response = announcementSearchUseCase.search(request);

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<AnnouncementDetailResponse> getById(
      @PathVariable("id")
      Long id
  ) {
    AnnouncementDetailResponse response = retrieveAnnouncementUseCase.findById(id);
    return ResponseEntity.ok(response);
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<IdResponse> update(
      @Login
      Long loginId,
      @PathVariable("id")
      Long id,
      @RequestBody
      @Valid
      UpdateAnnouncementRequest request
  ) {
    IdResponse response = updateAnnouncementUseCase.update(loginId, id, request);
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
