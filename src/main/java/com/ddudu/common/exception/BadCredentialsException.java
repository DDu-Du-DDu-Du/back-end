package com.ddudu.common.exception;

public class BadCredentialsException extends InvalidAuthenticationException {

  public BadCredentialsException(ErrorCode errorCode) {
    super(errorCode);
  }

}
