package com.ddudu.presentation.api.exception;

public class DuplicateResourceException extends BusinessException {

  public DuplicateResourceException(ErrorCode errorCode) {
    super(errorCode);
  }

}
