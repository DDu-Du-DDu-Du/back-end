package com.ddudu.common.exception;

import lombok.Getter;

@Getter
public class DefaultErrorCode implements ErrorCode {

  private static final int UNKNOWN_ERROR_CODE = 9999;
  private static final String DEFAULT_UNKNOWN_ERROR = "알 수 없는 예외입니다.";

  private final int code;
  private final String message;

  public DefaultErrorCode(String message) {
    this.code = UNKNOWN_ERROR_CODE;
    this.message = message;
  }

  public static DefaultErrorCode defaultMessage() {
    return new DefaultErrorCode(DEFAULT_UNKNOWN_ERROR);
  }

  @Override
  public String getCodeName() {
    return this.code + " DEFAULT";
  }

}
