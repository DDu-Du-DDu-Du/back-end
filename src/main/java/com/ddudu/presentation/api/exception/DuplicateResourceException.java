package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;

public class DuplicateResourceException extends BusinessException {

  public DuplicateResourceException(ErrorCode errorCode) {
    super(errorCode);
  }

}
