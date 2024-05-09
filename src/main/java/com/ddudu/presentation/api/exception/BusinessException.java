package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final int code;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    code = errorCode.getCode();
  }

}
