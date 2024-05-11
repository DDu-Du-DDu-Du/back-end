package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;

public class InvalidAuthenticationException extends BusinessException {

  public InvalidAuthenticationException(ErrorCode errorCode) {
    super(errorCode);
  }

}
