package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.announcement.aggregate.Announcement;

public interface AnnouncementCommandPort {

  Announcement save(Announcement announcement);

  Announcement update(Announcement announcement);

}
