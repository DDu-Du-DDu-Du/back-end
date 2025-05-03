package com.ddudu.bootstrap.common.exception;

import com.ddudu.domain.common.exception.ErrorCode;

public class DuplicateResourceException extends BusinessException {

  public DuplicateResourceException(ErrorCode errorCode) {
    super(errorCode);
  }

}
