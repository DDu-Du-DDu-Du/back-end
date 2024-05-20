package com.ddudu.application.service.goal;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class BaseGoalService {

  private final GoalLoaderPort goalLoaderPort;
  private final UserLoaderPort userLoaderPort;

  Goal findGoal(Long id) {
    return goalLoaderPort.findById(id)
        .orElseThrow(
            () -> new MissingResourceException(
                GoalErrorCode.ID_NOT_EXISTING.getCodeName(),
                Goal.class.getName(),
                id.toString()
            ));
  }

  User findUser(Long userId) {
    return userLoaderPort.loadMinimalUser(userId)
        .orElseThrow(
            () -> new MissingResourceException(
                GoalErrorCode.USER_NOT_EXISTING.getCodeName(),
                User.class.getName(),
                userId.toString()
            ));
  }

}
