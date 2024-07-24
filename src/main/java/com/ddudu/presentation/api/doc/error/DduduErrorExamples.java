package com.ddudu.presentation.api.doc.error;

public final class DduduErrorExamples {

  public static final String DDUDU_LOGIN_USER_NOT_EXISTING = """
      {
        "code": 2008,
        "message": "로그인 아이디가 존재하지 않습니다."
      }
      """;

  public static final String DDUDU_GOAL_NOT_EXISTING = """
      {
        "code": 2005,
        "message": "목표 아이디가 존재하지 않습니다."
      }
      """;

  public static final String DDUDU_INVALID_AUTHORITY = """
      {
        "code": 3009,
        "message": "해당 기능에 대한 사용자 권한이 없습니다."
      }
      """;

  public static final String DDUDU_USER_NOT_EXISTING = """
      {
        "code": 2006,
        "message": "사용자 아이디가 존재하지 않습니다."
      }
      """;

  public static final String DDUDU_EXCESSIVE_NAME_LENGTH = """
      {
        "code": 2003,
        "message": "할 일 내용이 최대 글자수를 초과했습니다."
      }
      """;

  public static final String DDUDU_ID_NOT_EXISTING = """
      {
        "code": 2004,
        "message": "할 일 아이디가 존재하지 않습니다."
      }
      """;

}
