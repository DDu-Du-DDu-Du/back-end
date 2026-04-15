package com.modoo.common.util;

public enum ListenerLogAction {
  START,
  END,
  SLOW,
  ERR;

  private static final String DOMAIN = "LISTENER";

  public String prefix() {
    return "[" + DOMAIN + "]" + "[" + this + "]";
  }
}
