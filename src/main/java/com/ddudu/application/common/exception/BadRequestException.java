package com.ddudu.application.common.exception;

public class BadRequestException extends BusinessException {

  public BadRequestException(ErrorCode errorCode) {
    super(errorCode);
  }

}
