package com.ddudu.application.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final int INVALID_INPUT_CODE = 1;
  private static final int INVALID_INPUT_TYPE_CODE = 2;
  private static final int INVALID_ENUM_FORMAT_CODE = 3;
  private static final int NOT_YET_HANDLED_EXCEPTION_CODE = 9998;
  private static final int UNKNOWN_EXCEPTION_CODE = 9999;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e
  ) {
    log.warn(e.getMessage(), e);

    List<ErrorResponse> responses = convertToErrorResponses(e);

    return ResponseEntity.badRequest()
        .body(responses);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e
  ) {
    log.warn(e.getMessage(), e);

    String message = formatMessageFrom(e);
    ErrorResponse response = ErrorResponse.from(INVALID_INPUT_TYPE_CODE, message);

    return ResponseEntity.badRequest()
        .body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidFormatException(InvalidFormatException e) {
    log.warn(e.getMessage(), e);

    String message = formatMessageFrom(e);
    ErrorResponse response = ErrorResponse.from(INVALID_ENUM_FORMAT_CODE, message);

    return ResponseEntity.badRequest()
        .body(response);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
    log.warn(e.getMessage(), e);

    ErrorResponse response = ErrorResponse.from(e);

    return ResponseEntity.badRequest()
        .body(response);
  }

  @ExceptionHandler(InvalidAuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedException(
      InvalidAuthenticationException e
  ) {
    log.warn(e.getMessage(), e);

    ErrorResponse response = ErrorResponse.from(e);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
        .body(response);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbiddenException(
      ForbiddenException e
  ) {
    log.warn(e.getMessage(), e);

    ErrorResponse response = ErrorResponse.from(e);

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(response);
  }

  @ExceptionHandler(DataNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException e) {
    log.warn(e.getMessage(), e);

    ErrorResponse response = ErrorResponse.from(e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(response);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
      DuplicateResourceException e
  ) {
    log.warn(e.getMessage(), e);

    ErrorResponse response = ErrorResponse.from(e);

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(response);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
    log.error(e.getMessage(), e);

    ErrorResponse response = ErrorResponse.from(NOT_YET_HANDLED_EXCEPTION_CODE, e.getMessage());

    return ResponseEntity.badRequest()
        .body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnknownException(Exception e) {
    log.error(e.getMessage(), e);

    ErrorResponse response = ErrorResponse.from(UNKNOWN_EXCEPTION_CODE, e.getMessage());

    return ResponseEntity.internalServerError()
        .body(response);
  }

  private List<ErrorResponse> convertToErrorResponses(MethodArgumentNotValidException e) {
    return e.getFieldErrors()
        .stream()
        .map(fieldError -> ErrorResponse.from(INVALID_INPUT_CODE, fieldError.getDefaultMessage()))
        .toList();
  }

  private String formatMessageFrom(MethodArgumentTypeMismatchException e) {
    String parameterName = e.getParameter()
        .getParameterName();

    return parameterName + "의 형식이 유효하지 않습니다.";
  }

  private String formatMessageFrom(InvalidFormatException e) {
    Class<?> targetType = e.getTargetType();
    String enumTypeName = targetType.getSimpleName();
    String validValues = Arrays.stream(targetType.getEnumConstants())
        .map(enumConstant -> ((Enum<?>) enumConstant).name())
        .collect(Collectors.joining(", "));

    return enumTypeName + "는 [" + validValues + "] 중 하나여야 합니다.";
  }

}
