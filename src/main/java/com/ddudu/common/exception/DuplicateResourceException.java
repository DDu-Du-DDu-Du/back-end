package com.ddudu.common.exception;

public class DuplicateResourceException extends BadRequestException {

  public DuplicateResourceException(ErrorCode errorCode) {
    super(errorCode);
  }

}
