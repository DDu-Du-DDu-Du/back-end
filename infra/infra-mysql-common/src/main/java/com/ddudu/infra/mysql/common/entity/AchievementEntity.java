package com.ddudu.infra.mysql.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AchievementEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false, length = 20)
  private String name;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "badge_image_url", nullable = false, length = 1024)
  private String badgeImageUrl;

}
