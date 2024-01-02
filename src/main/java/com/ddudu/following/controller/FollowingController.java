package com.ddudu.following.controller;

import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.response.FollowResponse;
import com.ddudu.following.service.FollowingService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/followings")
public class FollowingController {

  private final FollowingService followingService;

  @PostMapping
  public ResponseEntity<FollowResponse> create(FollowRequest request) {
    FollowResponse response = followingService.create(request);
    URI uri = URI.create("/api/followings/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

}
