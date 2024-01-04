package com.ddudu.common.exception;

public class InvalidAuthenticationException extends BusinessException {

  public InvalidAuthenticationException(ErrorCode errorCode) {
    super(errorCode);
  }

}
