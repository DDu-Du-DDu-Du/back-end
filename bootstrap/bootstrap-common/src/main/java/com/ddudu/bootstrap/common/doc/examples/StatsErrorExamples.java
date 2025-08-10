package com.ddudu.bootstrap.common.doc.examples;

public final class StatsErrorExamples {

  public static final String STATS_INVALID_DDUDU_STATS = """
      {
        "code": 9001,
        "message": "유효한 뚜두 상태가 아닙니다."
      }
      """;

  public static final String STATS_LOGIN_USER_NOT_FOUND = """
      {
        "code": 9002,
        "message": "로그인 사용자가 존재하지 않습니다."
      }
      """;

  public static final String STATS_USER_NOT_FOUND = """
      {
        "code": 9003,
        "message": "사용자가 존재하지 않습니다."
      }
      """;

  public static final String STATS_MONTHLY_STATS_EMPTY = """
      {
        "code": 9004,
        "message": "월 통계 데이터가 없습니다."
      }
      """;

  public static final String STATS_MONTHLY_MONTHLY_STATS_NOT_GROUPED_BY_GOAL = """
      {
        "code": 9005,
        "message": "다른 목표의 스탯이 포함되어 있습니다."
      }
      """;

  public static final String INVALID_TO_MONTH = """
      {
        "code": 9006,
        "message": "끝나는 월은 시작 월보다 빠를 수 없습니다."
      }
      """;

  public static final String NULL_GOAL_ID = """
      {
        "code": 9007,
        "message": "상세통계 대상 목표 아이디는 필수값입니다."
      }
      """;

}
