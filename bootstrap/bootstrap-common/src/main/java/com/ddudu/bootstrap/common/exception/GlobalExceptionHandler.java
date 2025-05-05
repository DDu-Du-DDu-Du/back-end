package com.ddudu.bootstrap.common.exception;

import com.ddudu.domain.common.exception.DefaultErrorCode;
import com.ddudu.domain.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
  // TODO: minimize custom exceptions in the handler parameters

  private static final int INVALID_INPUT_CODE = 1;
  private static final int INVALID_INPUT_TYPE_CODE = 2;
  private static final int INVALID_ENUM_FORMAT_CODE = 3;
  private static final int NOT_YET_HANDLED_EXCEPTION_CODE = 9998;

  private final ErrorCodeParser errorCodeParser;

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

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
    log.warn(e.getMessage(), e);

    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse response = ErrorResponse.from(errorCode);

    if (errorCode instanceof DefaultErrorCode) {
      return handleUnexpected(response);
    }

    return ResponseEntity.badRequest()
        .body(response);
  }

  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorized(UnsupportedOperationException e) {
    log.warn(e.getMessage(), e);

    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse response = ErrorResponse.from(errorCode);

    if (errorCode instanceof DefaultErrorCode) {
      return handleUnexpected(response);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(response);
  }

  @ExceptionHandler(MissingResourceException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(MissingResourceException e) {
    log.warn(e.getMessage(), e);

    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse response = ErrorResponse.from(errorCode);

    if (errorCode instanceof DefaultErrorCode) {
      return handleUnexpected(response);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(response);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
    log.warn(e.getMessage(), e);

    ErrorResponse response = ErrorResponse.from(e);

    return ResponseEntity.badRequest()
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

    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse response = ErrorResponse.from(errorCode);

    return ResponseEntity.internalServerError()
        .body(response);
  }

  private ResponseEntity<ErrorResponse> handleUnexpected(ErrorResponse response) {
    log.error("Exception is extremely unexpected: {}", response.message());

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
