package com.ddudu.following.controller;

import com.ddudu.auth.jwt.JwtAuthToken;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.response.FollowResponse;
import com.ddudu.following.service.FollowingService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/followings")
public class FollowingController {

  private final FollowingService followingService;

  @PostMapping
  public ResponseEntity<FollowResponse> create(
      Authentication authentication,
      @RequestBody
      @Valid
      FollowRequest request
  ) {
    long followerId = ((JwtAuthToken) authentication).getUserId();
    FollowResponse response = followingService.create(followerId, request);
    URI uri = URI.create("/api/followings/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

}
