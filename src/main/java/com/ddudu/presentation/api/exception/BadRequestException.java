package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;

public class BadRequestException extends BusinessException {

  public BadRequestException(ErrorCode errorCode) {
    super(errorCode);
  }

}
