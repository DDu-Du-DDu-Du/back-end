package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;

public class InvalidTokenException extends InvalidAuthenticationException {

  public InvalidTokenException(ErrorCode errorCode) {
    super(errorCode);
  }

}
