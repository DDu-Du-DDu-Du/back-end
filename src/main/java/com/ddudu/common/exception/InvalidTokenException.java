package com.ddudu.common.exception;

public class InvalidTokenException extends InvalidAuthenticationException {

  public InvalidTokenException(ErrorCode errorCode) {
    super(errorCode);
  }

}
