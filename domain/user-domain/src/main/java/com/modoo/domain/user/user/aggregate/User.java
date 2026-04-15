package com.modoo.domain.user.user.aggregate;

import static com.google.common.base.Preconditions.checkArgument;

import com.modoo.common.dto.Authority;
import com.modoo.common.exception.UserErrorCode;
import com.modoo.domain.user.user.aggregate.enums.UserStatus;
import com.modoo.domain.user.user.aggregate.enums.WeekStartDay;
import com.modoo.domain.user.user.aggregate.vo.AppConnectionOptions;
import com.modoo.domain.user.user.aggregate.vo.AuthProvider;
import com.modoo.domain.user.user.aggregate.vo.DisplayOptions;
import com.modoo.domain.user.user.aggregate.vo.MenuActivationItem;
import com.modoo.domain.user.user.aggregate.vo.MenuActivationOptions;
import com.modoo.domain.user.user.aggregate.vo.Options;
import com.modoo.domain.user.user.aggregate.vo.RealtimeSyncOptions;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  private static final int MAX_NICKNAME_LENGTH = 20;
  public static final int MAX_USERNAME_LENGTH = 30;
  private static final int MAX_INTRODUCTION_LENGTH = 50;
  private static final int MAX_URL_LENGTH = 1024;

  @EqualsAndHashCode.Include
  private final Long id;
  private final String username;
  private final String nickname;
  private final String introduction;
  private final Authority authority;
  private final UserStatus status;
  private final String profileImageUrl;

  @Getter(AccessLevel.NONE)
  private final Options options;
  private final List<AuthProvider> authProviders;

  @Builder
  private User(
      Long id,
      String username,
      String nickname,
      String introduction,
      Authority authority,
      UserStatus status,
      String profileImageUrl,
      List<AuthProvider> authProviders,
      Options options,
      Boolean allowingFollowsAfterApproval,
      Boolean templateNotification,
      Boolean todoNotification
  ) {
    validate(nickname, username, introduction, profileImageUrl);

    this.id = id;
    this.username = username;
    this.nickname = nickname;
    this.authority = Objects.isNull(authority) ? Authority.NORMAL : authority;
    this.introduction = Objects.nonNull(introduction) ? introduction.strip() : null;
    this.status = Objects.isNull(status) ? UserStatus.ACTIVE : status;
    this.profileImageUrl = profileImageUrl;
    this.options = Objects.isNull(options)
        ? buildOptions(allowingFollowsAfterApproval, templateNotification, todoNotification)
        : options;
    this.authProviders = Objects.isNull(authProviders) ? Collections.emptyList() : authProviders;
  }

  public boolean isAllowingFollowsAfterApproval() {
    return this.options.isAllowingFollowsAfterApproval();
  }

  public boolean isNotifyingTemplate() {
    return this.options.isTemplateNotification();
  }

  public boolean isNotifyingTodo() {
    return this.options.isTodoNotification();
  }

  public WeekStartDay getWeekStartDay() {
    return this.options.getDisplay()
        .getWeekStartDay();
  }

  public boolean isDarkMode() {
    return this.options.getDisplay()
        .isDarkMode();
  }

  public boolean isActiveCalendar() {
    return this.options.getMenuActivation()
        .getCalendar()
        .isActive();
  }

  public int getPriorityCalendar() {
    return this.options.getMenuActivation()
        .getCalendar()
        .getPriority();
  }

  public boolean isActiveDashboard() {
    return this.options.getMenuActivation()
        .getDashboard()
        .isActive();
  }

  public int getPriorityDashboard() {
    return this.options.getMenuActivation()
        .getDashboard()
        .getPriority();
  }

  public boolean isActiveStats() {
    return this.options.getMenuActivation()
        .getStats()
        .isActive();
  }

  public int getPriorityStats() {
    return this.options.getMenuActivation()
        .getStats()
        .getPriority();
  }

  public boolean isRealtimeSyncNotion() {
    return this.options.getAppConnection()
        .getRealtimeSync()
        .isNotion();
  }

  public boolean isRealtimeSyncGoogleCalendar() {
    return this.options.getAppConnection()
        .getRealtimeSync()
        .isGoogleCalendar();
  }

  public boolean isRealtimeSyncMicrosoftTodo() {
    return this.options.getAppConnection()
        .getRealtimeSync()
        .isMicrosoftTodo();
  }

  public boolean isAdmin() {
    return Objects.equals(this.authority, Authority.ADMIN);
  }

  public User applyProfileUpdate(String nickname, String introduction) {
    return User.builder()
        .id(this.id)
        .username(this.username)
        .nickname(nickname)
        .authority(this.authority)
        .introduction(introduction)
        .status(this.status)
        .profileImageUrl(this.profileImageUrl)
        .options(this.options)
        .build();
  }

  public User updateOptions(
      String weekStartDay,
      boolean isDarkMode,
      boolean isActiveCalendar,
      int priorityCalendar,
      boolean isActiveDashboard,
      int priorityDashboard,
      boolean isActiveStats,
      int priorityStats,
      boolean realtimeSyncNotion,
      boolean realtimeSyncGoogleCalendar,
      boolean realtimeSyncMicrosoftTodo
  ) {
    Options updatedOptions = Options.builder()
        .allowingFollowsAfterApproval(this.isAllowingFollowsAfterApproval())
        .templateNotification(this.isNotifyingTemplate())
        .todoNotification(this.isNotifyingTodo())
        .display(DisplayOptions.builder()
            .weekStartDay(WeekStartDay.get(weekStartDay))
            .darkMode(isDarkMode)
            .build())
        .menuActivation(MenuActivationOptions.builder()
            .calendar(MenuActivationItem.builder()
                .active(isActiveCalendar)
                .priority(priorityCalendar)
                .build())
            .dashboard(MenuActivationItem.builder()
                .active(isActiveDashboard)
                .priority(priorityDashboard)
                .build())
            .stats(MenuActivationItem.builder()
                .active(isActiveStats)
                .priority(priorityStats)
                .build())
            .build())
        .appConnection(AppConnectionOptions.builder()
            .realtimeSync(RealtimeSyncOptions.builder()
                .notion(realtimeSyncNotion)
                .googleCalendar(realtimeSyncGoogleCalendar)
                .microsoftTodo(realtimeSyncMicrosoftTodo)
                .build())
            .build())
        .build();

    return User.builder()
        .id(this.id)
        .username(this.username)
        .nickname(this.nickname)
        .introduction(this.introduction)
        .authority(this.authority)
        .status(this.status)
        .profileImageUrl(this.profileImageUrl)
        .authProviders(this.authProviders)
        .options(updatedOptions)
        .build();
  }

  private void validate(
      String nickname,
      String username,
      String introduction,
      String profileImageUrl
  ) {
    validateNickname(nickname);
    validateUsername(username);

    if (Objects.nonNull(introduction)) {
      validateIntroduction(introduction);
    }

    if (Objects.nonNull(profileImageUrl)) {
      validateUrl(profileImageUrl);
    }
  }

  private void validateNickname(String nickname) {
    checkArgument(StringUtils.isNotBlank(nickname), UserErrorCode.BLANK_NICKNAME.getCodeName());
    checkArgument(
        nickname.length() <= MAX_NICKNAME_LENGTH,
        UserErrorCode.EXCESSIVE_NICKNAME_LENGTH.getCodeName()
    );
  }

  private void validateUsername(String username) {
    checkArgument(StringUtils.isNotBlank(username), UserErrorCode.BLANK_USERNAME.getCodeName());
    checkArgument(
        username.length() <= MAX_USERNAME_LENGTH,
        UserErrorCode.EXCESSIVE_USERNAME_LENGTH.getCodeName()
    );
  }

  private void validateIntroduction(String introduction) {
    checkArgument(
        introduction.length() <= MAX_INTRODUCTION_LENGTH,
        UserErrorCode.EXCESSIVE_INTRODUCTION_LENGTH.getCodeName()
    );
  }

  private void validateUrl(String url) {
    checkArgument(
        url.length() <= MAX_URL_LENGTH,
        UserErrorCode.EXCESSIVE_PROFILE_IMAGE_URL_LENGTH.getCodeName()
    );
  }

  private Options buildOptions(
      Boolean allowingFollowsAfterApproval,
      Boolean templateNotification,
      Boolean todoNotification
  ) {
    return Options.builder()
        .allowingFollowsAfterApproval(allowingFollowsAfterApproval)
        .todoNotification(todoNotification)
        .templateNotification(templateNotification)
        .build();
  }

}
