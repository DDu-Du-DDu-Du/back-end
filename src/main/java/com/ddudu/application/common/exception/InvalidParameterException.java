package com.ddudu.application.common.exception;

public class InvalidParameterException extends BadRequestException {

  public InvalidParameterException(ErrorCode errorCode) {
    super(errorCode);
  }

}
