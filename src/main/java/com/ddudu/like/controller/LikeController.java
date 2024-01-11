package com.ddudu.like.controller;

import com.ddudu.common.annotation.Login;
import com.ddudu.like.dto.request.LikeRequest;
import com.ddudu.like.dto.response.LikeResponse;
import com.ddudu.like.service.LikeService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<LikeResponse> create(
      @Login
          Long loginId,
      @RequestBody
      @Valid
          LikeRequest request
  ) {
    LikeResponse response = likeService.create(loginId, request);
    URI uri = URI.create("/api/likes/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Login
          Long loginId,
      @PathVariable
          Long id
  ) {
    likeService.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<LikeResponse> getById(
      @PathVariable
          Long id
  ) {
    LikeResponse response = likeService.findById(id);

    return ResponseEntity.ok(response);
  }

}
