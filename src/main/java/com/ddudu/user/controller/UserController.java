package com.ddudu.user.controller;

import com.ddudu.common.annotation.Login;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private static final String PASSWORD_UPDATE_SUCCESS = "비밀번호가 성공적으로 변경되었습니다.";

  private final UserService userService;

  @PostMapping
  public ResponseEntity<SignUpResponse> signUp(
      @RequestBody
      @Valid
          SignUpRequest request
  ) {
    SignUpResponse response = userService.signUp(request);
    URI uri = URI.create("/api/users/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> validateToken(
      @Login
      Long loginId
  ) {
    UserResponse response = userService.findById(loginId);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}/email")
  public ResponseEntity<UserResponse> updateEmail(
      @PathVariable
          Long id,
      @RequestBody
      @Valid
          UpdateEmailRequest request

  ) {
    UserResponse response = userService.updateEmail(id, request);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}/password")
  public ResponseEntity<String> updatePassword(
      @PathVariable
          Long id,
      @RequestBody
      @Valid
          UpdatePasswordRequest request
  ) {
    userService.updatePassword(id, request);

    return ResponseEntity.ok(PASSWORD_UPDATE_SUCCESS);
  }

}
