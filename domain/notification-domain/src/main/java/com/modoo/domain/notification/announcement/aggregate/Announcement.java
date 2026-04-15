package com.modoo.domain.notification.announcement.aggregate;

import static com.google.common.base.Preconditions.checkArgument;

import com.modoo.common.exception.AnnouncementErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Announcement {

  private static final int MAX_TITLE_LENGTH = 50;
  private static final int MAX_CONTENTS_LENGTH = 2000;

  @EqualsAndHashCode.Include
  private final Long id;
  private final String title;
  private final String contents;
  private final Long userId;
  private final LocalDateTime createdAt;

  @Builder
  private Announcement(
      Long id,
      String title,
      String contents,
      Long userId,
      LocalDateTime createdAt
  ) {
    validate(title, contents, userId);

    this.id = id;
    this.title = title;
    this.contents = contents;
    this.userId = userId;
    this.createdAt = createdAt;
  }

  private void validate(String title, String contents, Long userId) {
    validateTitle(title);
    validateContents(contents);
    checkArgument(Objects.nonNull(userId), AnnouncementErrorCode.NULL_USER_ID.getCodeName());
  }

  private void validateTitle(String title) {
    checkArgument(StringUtils.isNotBlank(title), AnnouncementErrorCode.NULL_TITLE.getCodeName());
    checkArgument(
        title.length() <= MAX_TITLE_LENGTH,
        AnnouncementErrorCode.EXCESSIVE_TITLE_LENGTH.getCodeName()
    );
  }

  private void validateContents(String contents) {
    checkArgument(
        StringUtils.isNotBlank(contents),
        AnnouncementErrorCode.NULL_CONTENTS.getCodeName()
    );
    checkArgument(
        contents.length() <= MAX_CONTENTS_LENGTH,
        AnnouncementErrorCode.EXCESSIVE_CONTENTS_LENGTH.getCodeName()
    );
  }

  public Announcement update(String title, String contents) {
    return Announcement.builder()
        .id(this.id)
        .title(title)
        .contents(contents)
        .userId(this.userId)
        .createdAt(this.createdAt)
        .build();
  }

  public boolean isAuthor(Long userId) {
    return Objects.equals(this.userId, userId);
  }

}
