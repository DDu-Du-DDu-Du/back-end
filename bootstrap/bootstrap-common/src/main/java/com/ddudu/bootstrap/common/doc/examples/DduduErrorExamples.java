package com.ddudu.bootstrap.common.doc.examples;

public final class DduduErrorExamples {

  public static final String DDUDU_NULL_GOAL_VALUE = """
      {
        "code": 2001,
        "message": "목표는 필수값입니다."
      }
      """;

  public static final String DDUDU_BLANK_NAME = """
      {
        "code": 2002,
        "message": "할 일은 필수값입니다."
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

  public static final String DDUDU_GOAL_NOT_EXISTING = """
      {
        "code": 2005,
        "message": "목표 아이디가 존재하지 않습니다."
      }
      """;

  public static final String DDUDU_USER_NOT_EXISTING = """
      {
        "code": 2006,
        "message": "사용자 아이디가 존재하지 않습니다."
      }
      """;

  public static final String DDUDU_INVALID_AUTHORITY = """
      {
        "code": 2007,
        "message": "해당 기능에 대한 사용자 권한이 없습니다."
      }
      """;

  public static final String DDUDU_LOGIN_USER_NOT_EXISTING = """
      {
        "code": 2008,
        "message": "로그인 아이디가 존재하지 않습니다."
      }
      """;

  public static final String DDUDU_NULL_USER = """
      {
        "code": 2009,
        "message": "사용자는 필수값입니다."
      }
      """;

  public static final String DDUDU_UNABLE_TO_FINISH_BEFORE_BEGIN = """
      {
        "code": 2010,
        "message": "종료 시간은 시작 시간보다 뒤여야 합니다."
      }
      """;

  public static final String DDUDU_NULL_DATE_TO_MOVE = """
      {
        "code": 2011,
        "message": "변경할 날짜가 누락됐습니다."
      }
      """;

  public static final String DDUDU_SHOULD_POSTPONE_UNTIL_FUTURE = """
      {
        "code": 2012,
        "message": "미루기 날짜는 오늘 이후의 날짜여야 합니다."
      }
      """;

  public static final String DDUDU_UNABLE_TO_REPRODUCE_ON_SAME_DATE = """
      {
        "code": 2013,
        "message": "다시 하기의 날짜는 기존과 달라야합니다."
      }
      """;

  public static final String DDUDU_NEGATIVE_OR_ZERO_GOAL_ID = """
      {
        "code": 2014,
        "message": "목표 ID는 양수입니다."
      }
      """;

  public static final String DDUDU_NULL_SCHEDULED_DATE = """
      {
        "code": 2015,
        "message": "날짜는 필수값입니다."
      }
      """;

  public static final String GOAL_ALREADY_DONE = """
      {
        "code": 2016,
        "message": "목표가 이미 완료되었습니다."
      }
      """;

  public static final String BEGIN_AT_REQUIRED_FOR_REMINDER = """
      {
        "code": 2017,
        "message": "시작 시간 없이 미리알림을 설정할 수 없습니다."
      }
      """;

  public static final String REMINDER_NOT_AFTER_NOW = """
      {
        "code": 2018,
        "message": "미리알림 시간은 현재 시간보다 이후여야 합니다."
      }
      """;

  public static final String ZERO_REMINDER = """
      {
        "code": 2019,
        "message": "미리알림 입력값이 없습니다."
      }
      """;

  public static final String NEGATIVE_REMINDER_INPUT_EXISTS = """
      {
        "code": 2020,
        "message": "미리알림을 입력값 중 음수가 포함되어 있습니다."
      }
      """;


}
