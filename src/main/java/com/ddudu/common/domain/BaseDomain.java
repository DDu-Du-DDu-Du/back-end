package com.ddudu.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseDomain {

  private static final Boolean DEFAULT_IS_DELETED = false;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime updatedAt;

  private boolean isDeleted = DEFAULT_IS_DELETED;

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
