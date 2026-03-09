package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.announcement.aggregate.Announcement;

public interface AnnouncementLoaderPort {

  Announcement getAnnouncementOrElseThrow(Long id, String message);

}
