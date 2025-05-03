package com.ddudu.infrastructure.commonmysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateLikeEntity extends BaseEntity {

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
      name = "template_id",
      nullable = false
  )
  private Long templateId;

  @Column(
      name = "is_liked",
      nullable = false,
      columnDefinition = "TINYINT(1)"
  )
  private boolean isLiked;

}
