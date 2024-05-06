package com.ddudu.presentation.api.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final int code;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    code = errorCode.getCode();
  }

}
