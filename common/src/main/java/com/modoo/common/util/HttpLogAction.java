package com.modoo.common.util;

public enum HttpLogAction {
  REQ,
  RES,
  SLOW,
  ERR,
  AUTH;

  private static final String DOMAIN = "HTTP";

  public String prefix() {
    return "[" + DOMAIN + "]" + "[" + this + "]";
  }

  public String externalPrefix() {
    return "[EX_" + DOMAIN + "]" + "[" + this + "]";
  }

}
