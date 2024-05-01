package com.ddudu.application.like.controller;

import com.ddudu.application.common.annotation.Login;
import com.ddudu.application.like.dto.response.LikeResponse;
import com.ddudu.application.like.service.LikeService;
import com.ddudu.application.like.dto.request.LikeRequest;
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
public class LikeController {

  private final LikeService likeService;

  @PostMapping
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
