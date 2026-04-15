package com.modoo.infra.mysql.notification.briefing.repository;

import com.modoo.infra.mysql.notification.briefing.entity.DailyBriefingLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyBriefingRepository extends JpaRepository<DailyBriefingLogEntity, Long> {

  boolean existsByUserId(Long userId);

}
