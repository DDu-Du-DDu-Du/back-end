package com.ddudu.common.util;

import java.time.Duration;

public final class LogUtil {

  private static final long TEN_SECONDS = Duration.ofSeconds(10)
      .toMillis();

  public static boolean isSlow(long duration) {
    return duration >= TEN_SECONDS;
  }

  public static boolean isErrRes(int status) {
    return status >= 400;
  }

}
