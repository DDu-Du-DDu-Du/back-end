package com.ddudu.application.common.exception;

public class ForbiddenException extends BusinessException {

  public ForbiddenException(ErrorCode errorCode) {
    super(errorCode);
  }

}
