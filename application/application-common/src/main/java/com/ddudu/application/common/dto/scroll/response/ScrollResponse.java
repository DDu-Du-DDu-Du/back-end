package com.ddudu.application.common.dto.scroll.response;

import java.util.List;

public record ScrollResponse<T>(
    boolean isEmpty,
    List<T> contents,
    String nextCursor,
    boolean hasNext
) {

  public static <T> ScrollResponse<T> from(List<T> contents, String nextCursor) {
    return new ScrollResponse<T>(contents.isEmpty(), contents, nextCursor, nextCursor != null);
  }

}
