package com.ddudu.common.exception;

public record ErrorResponse(int code, String message) {

  public static ErrorResponse from(BusinessException e) {
    return from(e.getCode(), e.getMessage());
  }

  public static ErrorResponse from(int code, String message) {
    return new ErrorResponse(code, message);
  }
}
