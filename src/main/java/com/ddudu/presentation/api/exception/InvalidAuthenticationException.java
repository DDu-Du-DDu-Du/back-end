package com.ddudu.presentation.api.exception;

public class InvalidAuthenticationException extends BusinessException {

  public InvalidAuthenticationException(ErrorCode errorCode) {
    super(errorCode);
  }

}
