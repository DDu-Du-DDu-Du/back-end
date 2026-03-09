package com.ddudu.bootstrap.common.doc.examples;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnouncementErrorExamples {

  public static final String NULL_TITLE = """
      {
        "code": 13001,
        "message": "Announcement title is required."
      }
      """;

  public static final String EXCESSIVE_TITLE_LENGTH = """
      {
        "code": 13002,
        "message": "Announcement title must be 50 characters or fewer."
      }
      """;

  public static final String NULL_CONTENTS = """
      {
        "code": 13003,
        "message": "Announcement contents are required."
      }
      """;

  public static final String EXCESSIVE_CONTENTS_LENGTH = """
      {
        "code": 13004,
        "message": "Announcement contents must be 2000 characters or fewer."
      }
      """;

  public static final String LOGIN_USER_NOT_EXISTING = """
      {
        "code": 13006,
        "message": "Login user does not exist."
      }
      """;

  public static final String INVALID_AUTHORITY = """
      {
        "code": 13007,
        "message": "User is not authorized to perform this action."
      }
      """;

  public static final String ANNOUNCEMENT_NOT_EXISTING = """
      {
        "code": 13008,
        "message": "Announcement does not exist."
      }
      """;

}
