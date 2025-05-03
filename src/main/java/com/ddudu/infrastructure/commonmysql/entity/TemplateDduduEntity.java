package com.ddudu.infrastructure.commonmysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_ddudus")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateDduduEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "template_id",
      nullable = false
  )
  private Long template;

  @Column(
      name = "name",
      length = 50,
      nullable = false
  )
  private String name;

  @Column(name = "begin_at")
  private LocalDateTime beginAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @Column(
      name = "day_number",
      nullable = false,
      columnDefinition = "INT"
  )
  private int dayNumber;

}
