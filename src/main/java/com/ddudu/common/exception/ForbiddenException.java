package com.ddudu.common.exception;

public class ForbiddenException extends BusinessException {

  public ForbiddenException(ErrorCode errorCode) {
    super(errorCode);
  }

}
