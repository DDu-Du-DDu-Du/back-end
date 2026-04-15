package com.modoo.common.util;

public enum ExceptionLogAction {
  HANDLE;

  private static final String DOMAIN = "EXCEPTION";

  public String prefix() {
    return "[" + DOMAIN + "]" + "[" + this + "]";
  }
}
