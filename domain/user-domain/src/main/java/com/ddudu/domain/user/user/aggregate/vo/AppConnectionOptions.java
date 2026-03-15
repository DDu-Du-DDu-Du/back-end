package com.ddudu.domain.user.user.aggregate.vo;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AppConnectionOptions {

  private final RealtimeSyncOptions realtimeSync;

  @Builder
  private AppConnectionOptions(RealtimeSyncOptions realtimeSync) {
    this.realtimeSync = Objects.isNull(realtimeSync) ? RealtimeSyncOptions.builder().build()
        : realtimeSync;
  }

}
