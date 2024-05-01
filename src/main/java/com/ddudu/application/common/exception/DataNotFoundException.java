package com.ddudu.application.common.exception;

public class DataNotFoundException extends BusinessException {

  public DataNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

}
