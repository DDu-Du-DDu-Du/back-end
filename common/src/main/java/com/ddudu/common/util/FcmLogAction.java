package com.ddudu.common.util;

public enum FcmLogAction {
  SEND,
  DONE,
  FAIL,
  ERR;

  private static final String DOMAIN = "FCM";

  public String prefix() {
    return "[" + DOMAIN + "]" + "[" + this + "]";
  }
}
