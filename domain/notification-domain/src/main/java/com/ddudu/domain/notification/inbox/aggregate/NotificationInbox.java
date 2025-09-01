package com.ddudu.domain.notification.inbox.aggregate;

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
public class NotificationInbox {

  private Long id;
  private Long userId;
  private Long senderId;
  private Long eventId;
  private String typeCode;
  private String title;
  private String body;
  private LocalDateTime readAt;

}
