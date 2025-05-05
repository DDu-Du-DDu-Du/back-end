package com.ddudu.bootstrap.common.exception;

import com.ddudu.domain.common.exception.ErrorCode;

public class InvalidParameterException extends BadRequestException {

  public InvalidParameterException(ErrorCode errorCode) {
    super(errorCode);
  }

}
