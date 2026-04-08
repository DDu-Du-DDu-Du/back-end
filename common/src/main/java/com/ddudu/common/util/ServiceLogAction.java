package com.ddudu.common.util;

public enum ServiceLogAction {
  START,
  END,
  ERR;

  private static final String DOMAIN = "SERVICE";

  public String prefix() {
    return "[" + DOMAIN + "]" + "[" + this + "]";
  }
}
