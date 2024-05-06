package com.ddudu.presentation.api.exception;

public class BadCredentialsException extends InvalidAuthenticationException {

  public BadCredentialsException(ErrorCode errorCode) {
    super(errorCode);
  }

}
