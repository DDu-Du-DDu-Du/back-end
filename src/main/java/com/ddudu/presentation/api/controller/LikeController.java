package com.ddudu.presentation.api.controller;

import com.ddudu.old.like.dto.request.LikeRequest;
import com.ddudu.old.like.dto.response.LikeResponse;
import com.ddudu.old.like.service.LikeService;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.doc.LikeControllerDoc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController implements LikeControllerDoc {

  private final LikeService likeService;

  @PostMapping
  @Deprecated
  public ResponseEntity<LikeResponse> toggle(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      LikeRequest request
  ) {
    LikeResponse response = likeService.toggle(loginId, request);

    return ResponseEntity.ok(response);
  }

}
