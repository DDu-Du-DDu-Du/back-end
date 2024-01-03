package com.ddudu.common.exception;

public class InvalidParameterException extends BadRequestException {

  public InvalidParameterException(ErrorCode errorCode) {
    super(errorCode);
  }

}
