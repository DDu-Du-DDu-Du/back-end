package com.ddudu.bootstrap.common.exception;

import com.ddudu.domain.common.exception.ErrorCode;

public class ForbiddenException extends BusinessException {

  public ForbiddenException(ErrorCode errorCode) {
    super(errorCode);
  }

}
