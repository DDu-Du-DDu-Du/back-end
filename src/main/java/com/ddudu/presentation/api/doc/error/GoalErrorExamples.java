package com.ddudu.presentation.api.doc.error;

public final class GoalErrorExamples {

  public static final String GOAL_BLANK_NAME = """
      {
        "code": 3001,
        "message": "목표명은 필수값입니다."
      }
      """;

  public static final String GOAL_EXCESSIVE_NAME_LENGTH = """
      {
        "code": 3002,
        "message": "목표명이 최대 글자수를 초과했습니다."
      }
      """;

  public static final String GOAL_INVALID_COLOR_FORMAT = """
      {
        "code": 3003,
        "message": "올바르지 않은 색상 코드입니다."
      }
      """;

  public static final String GOAL_ID_NOT_EXISTING = """
      {
        "code": 3004,
        "message": "해당 아이디를 가진 목표가 존재하지 않습니다."
      }
      """;

  public static final String GOAL_NULL_STATUS = """
      {
        "code": 3005,
        "message": "목표 상태가 입력되지 않았습니다."
      }
      """;

  public static final String GOAL_BLANK_COLOR = """
      {
        "code": 3006,
        "message": "색상이 입력되지 않았습니다."
      }
      """;

  public static final String GOAL_NULL_PRIVACY_TYPE = """
      {
        "code": 3007,
        "message": "공개 설정이 입력되지 않았습니다."
      }
      """;

  public static final String GOAL_USER_NOT_EXISTING = """
      {
        "code": 3008,
        "message": "해당 아이디를 가진 사용자가 존재하지 않습니다."
      }
      """;

  public static final String GOAL_INVALID_AUTHORITY = """
      {
        "code": 3009,
        "message": "해당 기능에 대한 사용자 권한이 없습니다."
      }
      """;

  public static final String GOAL_INVALID_PRIVACY_TYPE = """
      {
        "code": 3010,
        "message": "존재하지 않은 공개 설정입니다."
      }
      """;

  public static final String GOAL_NULL_USER = """
      {
        "code": 3011,
        "message": "사용자는 필수값입니다."
      }
      """;

  public static final String GOAL_INVALID_GOAL_STATUS = """
      {
        "code": 3012,
        "message": "존재하지 않은 목표 상태입니다."
      }
      """;

  public static final String GOAL_TWO_OWNERS = """
      {
        "code": 3013,
        "message": "목표는 두 명 이상의 소유자를 가질 수 없습니다."
      }
      """;

  public static final String GOAL_NOT_POSITIVE_USER_ID = """
      {
        "code": 3014,
        "message": "사용자 아이디는 양수입니다."
      }
      """;

}
