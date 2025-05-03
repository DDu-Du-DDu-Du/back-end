package com.ddudu.bootstrap.common.exception;

import com.ddudu.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final int code;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    code = errorCode.getCode();
  }

}
