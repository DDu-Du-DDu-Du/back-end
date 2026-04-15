package com.modoo.infra.mysql.notification.announcement.repository;

import com.modoo.infra.mysql.notification.announcement.entity.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long>,
    AnnouncementQueryRepository {

}
