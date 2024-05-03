package com.ddudu.persistence.entity;

import com.ddudu.application.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "period_goals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeriodGoalEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(name = "contents")
  private String contents;

  @Column(name = "type", nullable = false, length = 15)
  private String type;

  @Column(name = "plan_date", nullable = false, columnDefinition = "DATE")
  private LocalDate planDate;

}
