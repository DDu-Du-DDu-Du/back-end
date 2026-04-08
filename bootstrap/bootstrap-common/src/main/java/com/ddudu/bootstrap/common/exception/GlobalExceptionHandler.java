package com.ddudu.bootstrap.common.exception;

import com.ddudu.common.exception.DefaultErrorCode;
import com.ddudu.common.exception.ErrorCode;
import com.ddudu.common.exception.ErrorCodeParser;
import com.ddudu.common.util.ExceptionLogAction;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  private static final int INVALID_INPUT_CODE = 1;
  private static final int INVALID_INPUT_TYPE_CODE = 2;
  private static final int INVALID_ENUM_FORMAT_CODE = 3;
  private static final int NOT_YET_HANDLED_EXCEPTION_CODE = 9998;

  private final ErrorCodeParser errorCodeParser;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e,
      HttpServletRequest request
  ) {
    List<ErrorResponse> errorResponses = convertToErrorResponses(e);
    ResponseEntity<List<ErrorResponse>> response = ResponseEntity.badRequest()
        .body(errorResponses);

    logWarn(e, request, response.getStatusCode(), errorResponses.get(0));

    return response;
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e,
      HttpServletRequest request
  ) {
    String message = formatMessageFrom(e);
    ErrorResponse errorResponse = ErrorResponse.from(INVALID_INPUT_TYPE_CODE, message);
    ResponseEntity<ErrorResponse> response = ResponseEntity.badRequest()
        .body(errorResponse);

    logWarn(e, request, response.getStatusCode(), errorResponse);

    return response;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidFormatException(
      InvalidFormatException e,
      HttpServletRequest request
  ) {
    String message = formatMessageFrom(e);
    ErrorResponse errorResponse = ErrorResponse.from(INVALID_ENUM_FORMAT_CODE, message);
    ResponseEntity<ErrorResponse> response = ResponseEntity.badRequest()
        .body(errorResponse);

    logWarn(e, request, response.getStatusCode(), errorResponse);

    return response;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(
      IllegalArgumentException e,
      HttpServletRequest request
  ) {
    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse errorResponse = ErrorResponse.from(errorCode);

    if (errorCode instanceof DefaultErrorCode) {
      return handleUnexpected(errorResponse);
    }

    ResponseEntity<ErrorResponse> response = ResponseEntity.badRequest()
        .body(errorResponse);

    logWarn(e, request, response.getStatusCode(), errorResponse);

    return response;
  }

  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorized(
      UnsupportedOperationException e,
      HttpServletRequest request
  ) {
    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse errorResponse = ErrorResponse.from(errorCode);

    if (errorCode instanceof DefaultErrorCode) {
      return handleUnexpected(errorResponse);
    }

    ResponseEntity<ErrorResponse> response = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse);

    logWarn(e, request, response.getStatusCode(), errorResponse);

    return response;
  }

  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<ErrorResponse> handleForbidden(
      SecurityException e,
      HttpServletRequest request
  ) {
    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse errorResponse = ErrorResponse.from(errorCode);

    if (errorCode instanceof DefaultErrorCode) {
      return handleUnexpected(errorResponse);
    }

    ResponseEntity<ErrorResponse> response = ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(errorResponse);

    logWarn(e, request, response.getStatusCode(), errorResponse);

    return response;
  }

  @ExceptionHandler(MissingResourceException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      MissingResourceException e,
      HttpServletRequest request
  ) {
    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse errorResponse = ErrorResponse.from(errorCode);

    if (errorCode instanceof DefaultErrorCode) {
      return handleUnexpected(errorResponse);
    }

    ResponseEntity<ErrorResponse> response = ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(errorResponse);

    logWarn(e, request, response.getStatusCode(), errorResponse);

    return response;
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleUnprocessableEntity(
      IllegalStateException e,
      HttpServletRequest request
  ) {
    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse errorResponse = ErrorResponse.from(errorCode);

    if (errorCode instanceof DefaultErrorCode) {
      return handleUnexpected(errorResponse);
    }

    ResponseEntity<ErrorResponse> response = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(errorResponse);

    logWarn(e, request, response.getStatusCode(), errorResponse);

    return response;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnknownException(
      Exception e,
      HttpServletRequest request
  ) {
    ErrorCode errorCode = errorCodeParser.parse(e.getMessage());
    ErrorResponse errorResponse = ErrorResponse.from(errorCode);
    ResponseEntity<ErrorResponse> response = ResponseEntity.internalServerError()
        .body(errorResponse);

    logException(e, request, response.getStatusCode(), errorResponse, true);

    return response;
  }

  private ResponseEntity<ErrorResponse> handleUnexpected(ErrorResponse response) {
    log.error("Exception is extremely unexpected: {}", response.message());

    return ResponseEntity.internalServerError()
        .body(response);
  }

  private List<ErrorResponse> convertToErrorResponses(MethodArgumentNotValidException e) {
    return e.getFieldErrors()
        .stream()
        .map(this::parseFromFieldError)
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

  private ErrorResponse parseFromFieldError(FieldError fieldError) {
    ErrorCode errorCode = errorCodeParser.parse(fieldError.getDefaultMessage());

    return ErrorResponse.from(errorCode);
  }

  private void logWarn(
      Exception e,
      HttpServletRequest request,
      HttpStatusCode status,
      ErrorResponse response
  ) {
    logException(e, request, status, response, false);
  }

  private void logException(
      Exception e,
      HttpServletRequest request,
      HttpStatusCode status,
      ErrorResponse response,
      boolean isUnknown
  ) {
    String exceptionSimpleName = e.getClass()
        .getSimpleName();
    String logMessage = "{} uri={} status={} exception={} code={} message={}";

    if (isUnknown) {
      log.error(
          logMessage,
          ExceptionLogAction.HANDLE.prefix(),
          request.getRequestURI(),
          status,
          exceptionSimpleName,
          response.code(),
          response.message(),
          e
      );

      return;
    }

    log.warn(
        logMessage,
        ExceptionLogAction.HANDLE.prefix(),
        request.getRequestURI(),
        status,
        exceptionSimpleName,
        response.code(),
        response.message(),
        e
    );
  }

}
