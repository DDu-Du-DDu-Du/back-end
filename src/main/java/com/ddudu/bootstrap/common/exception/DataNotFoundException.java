package com.ddudu.bootstrap.common.exception;

import com.ddudu.domain.common.exception.ErrorCode;

public class DataNotFoundException extends BusinessException {

  public DataNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

}
