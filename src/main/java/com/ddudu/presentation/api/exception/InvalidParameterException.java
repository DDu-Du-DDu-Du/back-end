package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;

public class InvalidParameterException extends BadRequestException {

  public InvalidParameterException(ErrorCode errorCode) {
    super(errorCode);
  }

}
