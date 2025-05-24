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

}
