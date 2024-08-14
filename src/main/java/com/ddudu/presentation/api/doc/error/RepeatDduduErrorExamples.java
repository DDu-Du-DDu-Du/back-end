package com.ddudu.presentation.api.doc.error;

public final class RepeatDduduErrorExamples {

  public static final String REPEAT_DDUDU_BLANK_NAME = """
      {
        "code": 6001,
        "message": "반복 뚜두명은 필수값입니다."
      }
      """;

  public static final String REPEAT_DDUDU_NULL_GOAL_VALUE = """
      {
        "code": 6002,
        "message": "목표는 필수값입니다."
      }
      """;

  public static final String REPEAT_DDUDU_NULL_REPEAT_TYPE = """
      {
        "code": 6003,
        "message": "반복 유형은 필수값입니다."
      }
      """;

  public static final String REPEAT_DDUDU_NULL_START_DATE = """
      {
        "code": 6004,
        "message": "반복 시작 날짜는 필수값입니다."
      }
      """;

  public static final String REPEAT_DDUDU_NULL_END_DATE = """
      {
        "code": 6005,
        "message": "반복 종료 날짜는 필수값입니다."
      }
      """;

  public static final String REPEAT_DDUDU_EXCESSIVE_NAME_LENGTH = """
      {
        "code": 6006,
        "message": "반복 뚜두명이 최대 글자수를 초과했습니다."
      }
      """;

  public static final String REPEAT_DDUDU_UNABLE_TO_END_BEFORE_START = """
      {
        "code": 6007,
        "message": "종료 날짜는 시작 날짜보다 뒤여야 합니다."
      }
      """;

  public static final String REPEAT_DDUDU_UNABLE_TO_FINISH_BEFORE_BEGIN = """
      {
        "code": 6008,
        "message": "종료 시간은 시작 시간보다 뒤여야 합니다."
      }
      """;

  public static final String REPEAT_DDUDU_INVALID_REPEAT_TYPE = """
      {
        "code": 6009,
        "message": "유효하지 않은 반복 유형입니다."
      }
      """;

  public static final String REPEAT_DDUDU_INVALID_DAY_OF_WEEK = """
      {
        "code": 6010,
        "message": "유효하지 않은 요일입니다."
      }
      """;

  public static final String REPEAT_DDUDU_NULL_OR_EMPTY_REPEAT_DATES_OF_MONTH = """
      {
        "code": 6011,
        "message": "반복되는 날짜가 없습니다."
      }
      """;

  public static final String REPEAT_DDUDU_NULL_OR_EMPTY_REPEAT_DAYS_OF_WEEK = """
      {
        "code": 6012,
        "message": "반복되는 요일이 없습니다."
      }
      """;

  public static final String REPEAT_DDUDU_NULL_LAST_DAY = """
      {
        "code": 6013,
        "message": "마지막 날 반복 여부는 필수값입니다."
      }
      """;

  public static final String REPEAT_DDUDU_INVALID_GOAL = """
      {
        "code": 6014,
        "message": "유효하지 않은 목표입니다."
      }
      """;

  public static final String REPEAT_DDUDU_NOT_EXIST = """
      {
        "code": 6015,
        "message": "해당 아이디를 가진 반복 뚜두가 존재하지 않습니다."
      }
      """;

}
