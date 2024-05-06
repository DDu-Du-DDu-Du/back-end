package com.ddudu.presentation.api.exception;

public class InvalidTokenException extends InvalidAuthenticationException {

  public InvalidTokenException(ErrorCode errorCode) {
    super(errorCode);
  }

}
