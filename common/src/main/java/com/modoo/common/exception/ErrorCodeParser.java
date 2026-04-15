package com.modoo.common.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ErrorCodeParser {

  public ErrorCode parse(String message) {
    if (StringUtils.isBlank(message)) {
      return DefaultErrorCode.defaultMessage();
    }

    String[] codeName = message.split(" ");
    String code = codeName[0];
    String prefix = code.substring(0, code.length() - 3);
    String adjusted = prefix.length() < 2 ? "0" + prefix : prefix;
    String name = codeName[1];

    return switch (adjusted) {
      case "01" -> UserErrorCode.valueOf(name);
      case "02" ->
          code.charAt(1) == '1' ? ReminderErrorCode.valueOf(name) : TodoErrorCode.valueOf(name);
      case "03" -> GoalErrorCode.valueOf(name);
      case "04" -> PeriodGoalErrorCode.valueOf(name);
      case "05" -> AuthErrorCode.valueOf(name);
      case "06" -> RepeatTodoErrorCode.valueOf(name);
      case "07" -> LikeErrorCode.valueOf(name);
      case "08" -> FollowingErrorCode.valueOf(name);
      case "09" ->
          code.charAt(1) != '9' ? StatsErrorCode.valueOf(name) : new DefaultErrorCode(message);
      case "10" -> NotificationEventErrorCode.valueOf(name);
      case "11" -> DailyBriefingLogErrorCode.valueOf(name);
      case "12" -> NotificationDeviceTokenErrorCode.valueOf(name);
      default -> new DefaultErrorCode(message);
    };
  }

}
