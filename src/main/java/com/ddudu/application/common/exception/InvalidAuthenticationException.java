package com.ddudu.application.common.exception;

public class InvalidAuthenticationException extends BusinessException {

  public InvalidAuthenticationException(ErrorCode errorCode) {
    super(errorCode);
  }

}
