package com.ddudu.presentation.api.exception;

public class InvalidParameterException extends BadRequestException {

  public InvalidParameterException(ErrorCode errorCode) {
    super(errorCode);
  }

}
