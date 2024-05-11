package com.ddudu.presentation.api.exception;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.exception.UserErrorCode;
import com.ddudu.application.exception.DefaultErrorCode;
import com.ddudu.application.exception.ErrorCode;
import com.ddudu.old.like.exception.LikeErrorCode;
import com.ddudu.old.todo.exception.TodoErrorCode;
import com.ddudu.old.user.exception.FollowingErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ErrorCodeParser {

  public ErrorCode parse(String message) {
    String[] codeName = message.split(" ");
    String code = codeName[0];
    String name = codeName[1];

    return switch (code.charAt(0)) {
      case '1' -> UserErrorCode.valueOf(name);
      case '2' -> TodoErrorCode.valueOf(name);
      case '3' -> GoalErrorCode.valueOf(name);
      case '5' -> AuthErrorCode.valueOf(name);
      case '6' -> FollowingErrorCode.valueOf(name);
      case '7' -> LikeErrorCode.valueOf(name);
      default -> new DefaultErrorCode(message);
    };

  }

}
