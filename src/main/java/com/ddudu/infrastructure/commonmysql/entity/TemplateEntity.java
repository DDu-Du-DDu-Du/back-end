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
@Table(name = "templates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "goal_id")
  private Long goalId;

  @Column(
      name = "created_by",
      nullable = false
  )
  private Long createdBy;

  @Column(
      name = "title",
      nullable = false,
      length = 50
  )
  private String title;

  @Column(
      name = "description",
      nullable = false,
      length = 1000
  )
  private String description;

  @Column(
      name = "time_estimation",
      length = 20
  )
  private String timeEstimation;

  @Column(
      name = "sharing_message",
      length = 100
  )
  private String sharingMessage;

}
