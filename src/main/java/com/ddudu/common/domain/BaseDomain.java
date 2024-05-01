package com.ddudu.common.domain;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class BaseDomain {

  private static final Boolean DEFAULT_IS_DELETED = false;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime updatedAt;

  private boolean isDeleted;

  public BaseDomain(LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isDeleted) {
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.isDeleted = isNull(isDeleted) ? DEFAULT_IS_DELETED : isDeleted;
  }

  public void delete() {
    if (!isDeleted) {
      isDeleted = true;
    }
  }

  public void undelete() {
    if (isDeleted) {
      isDeleted = false;
    }
  }

}
