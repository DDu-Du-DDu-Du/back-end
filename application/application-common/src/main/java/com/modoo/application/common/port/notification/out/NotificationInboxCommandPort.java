package com.modoo.application.common.port.notification.out;

import com.modoo.domain.notification.event.aggregate.NotificationInbox;

public interface NotificationInboxCommandPort {

  NotificationInbox save(NotificationInbox notificationInbox);

  NotificationInbox update(NotificationInbox notificationInbox);

}
