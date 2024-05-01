package com.ddudu.application.common.exception;

public class InvalidTokenException extends InvalidAuthenticationException {

  public InvalidTokenException(ErrorCode errorCode) {
    super(errorCode);
  }

}
