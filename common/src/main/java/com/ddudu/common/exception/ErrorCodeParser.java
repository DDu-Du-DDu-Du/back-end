package com.ddudu.common.exception;

import org.springframework.stereotype.Component;

@Component
public class ErrorCodeParser {

  public ErrorCode parse(String message) {
    String[] codeName = message.split(" ");
    String code = codeName[0];
    String name = codeName[1];

    return switch (code.charAt(0)) {
      case '1' -> UserErrorCode.valueOf(name);
      case '2' -> DduduErrorCode.valueOf(name);
      case '3' -> GoalErrorCode.valueOf(name);
      case '4' -> PeriodGoalErrorCode.valueOf(name);
      case '5' -> AuthErrorCode.valueOf(name);
      case '6' -> RepeatDduduErrorCode.valueOf(name);
      case '7' -> LikeErrorCode.valueOf(name);
      case '8' -> FollowingErrorCode.valueOf(name);
      case '9' ->
          code.charAt(1) != '9' ? StatsErrorCode.valueOf(name) : new DefaultErrorCode(message);
      default -> new DefaultErrorCode(message);
    };
  }

}
