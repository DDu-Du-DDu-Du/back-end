package com.ddudu.persistence.entity;

import com.ddudu.application.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateLikeEntity extends BaseEntity {

  @EmbeddedId
  private TemplateLikeId id;

  @Column(name = "is_liked", nullable = false, columnDefinition = "TINYINT(1)")
  boolean isLiked;

}
