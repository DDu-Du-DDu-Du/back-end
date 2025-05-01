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
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "receiver_id",
      nullable = false
  )
  private UserEntity receiver;

  @Column(
      name = "message",
      nullable = false
  )
  private String message;

  @Column(
      name = "type",
      nullable = false,
      length = 15
  )
  private String type;

  @Column(
      name = "is_read",
      nullable = false
  )
  private boolean isRead;

}
