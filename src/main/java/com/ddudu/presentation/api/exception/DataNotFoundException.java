package com.ddudu.presentation.api.exception;

import com.ddudu.application.exception.ErrorCode;

public class DataNotFoundException extends BusinessException {

  public DataNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

}
