package com.ddudu.application.exception;

import lombok.Getter;

@Getter
public class DefaultErrorCode implements ErrorCode {

  private static final int UNKNOWN_ERROR_CODE = 9999;

  private final int code;
  private final String message;

  public DefaultErrorCode(String message) {
    this.code = UNKNOWN_ERROR_CODE;
    this.message = message;
  }

}
