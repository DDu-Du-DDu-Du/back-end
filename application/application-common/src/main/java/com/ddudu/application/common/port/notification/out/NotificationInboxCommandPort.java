package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.event.aggregate.NotificationInbox;

public interface NotificationInboxCommandPort {

  NotificationInbox save(NotificationInbox notificationInbox);

  NotificationInbox update(NotificationInbox notificationInbox);

}
