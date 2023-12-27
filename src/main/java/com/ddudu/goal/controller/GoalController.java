package com.ddudu.goal.controller;

import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.ErrorResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.service.GoalService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Slf4j
public class GoalController {

  private final GoalService goalService;

  @PostMapping
  public ResponseEntity<CreateGoalResponse> create(
      @RequestBody
      @Valid
      CreateGoalRequest request
  ) {
    CreateGoalResponse response = goalService.create(request);
    URI uri = URI.create("/api/goals/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<GoalResponse> update(
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateGoalRequest request
  ) {
    GoalResponse response = goalService.update(id, request);

    return ResponseEntity.ok(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentEx(IllegalArgumentException e) {
    ErrorResponse response = new ErrorResponse(e.getMessage());

    return ResponseEntity.badRequest()
        .body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidEx(
      MethodArgumentNotValidException e
  ) {
    String errorMessage = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toList()
        .toString();

    ErrorResponse response = new ErrorResponse(errorMessage);

    return ResponseEntity.badRequest()
        .body(response);
  }

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<ErrorResponse> handleInvalidFormatEx(
      InvalidFormatException e
  ) {
    Class<?> targetType = e.getTargetType();
    String enumTypeName = targetType.getSimpleName();
    String validValues = Arrays.stream(targetType.getEnumConstants())
        .map(enumConstant -> ((Enum<?>) enumConstant).name())
        .collect(Collectors.joining(", "));

    return ResponseEntity.badRequest()
        .body(new ErrorResponse(enumTypeName + "는 [" + validValues + "] 중 하나여야 합니다."));
  }

}
