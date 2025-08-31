package com.ddudu.infra.mysql.notification.briefing.entity;

import com.ddudu.domain.notification.briefing.aggregate.DailyBriefingLog;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_briefing_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DailyBriefingLogEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

  @Column(
      name = "briefing_date",
      nullable = false,
      columnDefinition = "DATE"
  )
  private LocalDate briefingDate;

  public static DailyBriefingLogEntity from(DailyBriefingLog domain) {
    return DailyBriefingLogEntity.builder()
        .id(domain.getId())
        .userId(domain.getUserId())
        .briefingDate(domain.getBriefingDate())
        .build();
  }

  public DailyBriefingLog toDomain() {
    return DailyBriefingLog.builder()
        .id(id)
        .userId(userId)
        .briefingDate(briefingDate)
        .build();
  }

}
