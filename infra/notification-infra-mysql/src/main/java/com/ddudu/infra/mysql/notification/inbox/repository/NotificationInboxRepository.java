package com.ddudu.infra.mysql.notification.inbox.repository;

import com.ddudu.infra.mysql.notification.inbox.entity.NotificationInboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationInboxRepository extends JpaRepository<NotificationInboxEntity, Long>,
    NotificationInboxQueryRepository {

}
