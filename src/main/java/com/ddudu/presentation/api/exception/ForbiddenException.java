package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;

public class ForbiddenException extends BusinessException {

  public ForbiddenException(ErrorCode errorCode) {
    super(errorCode);
  }

}
