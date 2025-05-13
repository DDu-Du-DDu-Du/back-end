package com.ddudu.bootstrap.common.exception;

import com.ddudu.common.exception.ErrorCode;
import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {

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
