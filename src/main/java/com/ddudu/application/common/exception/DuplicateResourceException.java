package com.ddudu.application.common.exception;

public class DuplicateResourceException extends BusinessException {

  public DuplicateResourceException(ErrorCode errorCode) {
    super(errorCode);
  }

}
