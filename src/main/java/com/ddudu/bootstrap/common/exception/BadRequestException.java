package com.ddudu.bootstrap.common.exception;

import com.ddudu.domain.common.exception.ErrorCode;

public class BadRequestException extends BusinessException {

  public BadRequestException(ErrorCode errorCode) {
    super(errorCode);
  }

}
