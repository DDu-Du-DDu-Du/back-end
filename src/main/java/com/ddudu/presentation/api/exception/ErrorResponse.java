package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;
import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {

  // TODO: 나중에 지우기
  public static ErrorResponse from(BusinessException e) {
    return from(e.getCode(), e.getMessage());
  }

  public static ErrorResponse from(ErrorCode errorCode) {
    return from(errorCode.getCode(), errorCode.getMessage());
  }

  public static ErrorResponse from(int code, String message) {
    return ErrorResponse.builder()
        .code(code)
        .message(message)
        .build();
  }

}
