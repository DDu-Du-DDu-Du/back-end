package com.modoo.common.util;

public enum SchedulerLogAction {
  REG,
  BATCH,
  TRIG,
  SUCC,
  SLOW,
  ERR;

  private static final String DOMAIN = "SCHEDULER";

  public String prefix() {
    return "[" + DOMAIN + "]" + "[" + this + "]";
  }
}
