package com.ddudu.application.exception;

public interface ErrorCode {

  int getCode();

  String getMessage();

  // TODO: remove default after migration
  default String getCodeName() {
    return null;
  }

}
