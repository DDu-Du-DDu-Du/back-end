package com.ddudu.domain.notification.event.aggregate;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationEvent {

  private Long id;
  private String typeCode;
  private Long senderId;
  private Long receiverId;
  private Long contextId;
  private LocalDateTime firedAt;

}
