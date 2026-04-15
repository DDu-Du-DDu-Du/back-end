package com.modoo.application.common.port.notification.out;

import com.modoo.domain.notification.announcement.aggregate.Announcement;

public interface AnnouncementCommandPort {

  Announcement save(Announcement announcement);

  Announcement update(Announcement announcement);

}
