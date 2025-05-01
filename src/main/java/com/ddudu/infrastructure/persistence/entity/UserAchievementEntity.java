package com.ddudu.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_achievements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAchievementEntity extends BaseEntity {

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
      name = "achievement_id",
      nullable = false
  )
  private Long achievementEntityId;

  @Column(
      name = "is_main",
      nullable = false
  )
  private boolean isMain;

}
