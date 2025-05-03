package com.ddudu.bootstrap.common.exception;

import com.ddudu.domain.user.auth.exception.AuthErrorCode;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.user.user.exception.UserErrorCode;
import com.ddudu.domain.common.exception.DefaultErrorCode;
import com.ddudu.domain.common.exception.ErrorCode;
import com.ddudu.domain.planning.ddudu.exception.LikeErrorCode;
import com.ddudu.domain.user.user.exception.FollowingErrorCode;
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
      case '5' -> AuthErrorCode.valueOf(name);
      case '6' -> FollowingErrorCode.valueOf(name);
      case '7' -> LikeErrorCode.valueOf(name);
      default -> new DefaultErrorCode(message);
    };

  }

}
