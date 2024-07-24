package com.ddudu.presentation.api.doc.error;

public final class RepeatDduduErrorExamples {

  public static final String REPEAT_DDUDU_NULL_GOAL_VALUE = """
      {
        "code": 6002,
        "message": "목표는 필수값입니다."
      }
      """;

  public static final String REPEAT_DDUDU_INVALID_GOAL = """
      {
        "code": 6014,
        "message": "유효하지 않은 목표입니다."
      }
      """;

}
