package com.ddudu.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {

  @Column(
      name = "created_at",
      nullable = false,
      columnDefinition = "TIMESTAMP"
  )
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(
      name = "updated_at",
      nullable = false,
      columnDefinition = "TIMESTAMP"
  )
  @LastModifiedDate
  private LocalDateTime updatedAt;

}
