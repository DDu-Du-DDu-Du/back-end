package com.ddudu.presentation.api.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {

  public static ErrorResponse from(BusinessException e) {
    return from(e.getCode(), e.getMessage());
  }

  public static ErrorResponse from(int code, String message) {
    return ErrorResponse.builder()
        .code(code)
        .message(message)
        .build();
  }

}
