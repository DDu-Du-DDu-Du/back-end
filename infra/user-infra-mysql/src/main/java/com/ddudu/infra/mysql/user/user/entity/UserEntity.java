package com.ddudu.infra.mysql.user.user.entity;

import com.ddudu.common.dto.Authority;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.User.UserBuilder;
import com.ddudu.domain.user.user.aggregate.enums.UserStatus;
import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import com.ddudu.domain.user.user.aggregate.vo.AppConnectionOptions;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.domain.user.user.aggregate.vo.DisplayOptions;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationItem;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationOptions;
import com.ddudu.domain.user.user.aggregate.vo.Options;
import com.ddudu.domain.user.user.aggregate.vo.RealtimeSyncOptions;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "nickname",
      length = 20,
      nullable = false
  )
  private String nickname;

  @Column(
      name = "username",
      length = 30,
      unique = true
  )
  private String username;

  @Column(
      name = "introduction",
      length = 50
  )
  private String introduction;

  @Column(
      name = "profile_image_url",
      length = 1024
  )
  private String profileImageUrl;

  @Column(
      name = "authority",
      columnDefinition = "VARCHAR",
      length = 15
  )
  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Column(
      name = "status",
      columnDefinition = "VARCHAR",
      length = 20
  )
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(
      name = "follows_after_approval",
      nullable = false
  )
  private boolean allowingFollowsAfterApproval;

  @Column(
      name = "template_notification",
      nullable = false
  )
  private boolean templateNotification;

  @Column(
      name = "ddudu_notification",
      nullable = false
  )
  private boolean dduduNotification;

  @Column(
      name = "week_start_day",
      length = 3,
      nullable = false
  )
  @Enumerated(EnumType.STRING)
  private WeekStartDay weekStartDay;

  @Column(
      name = "dark_mode",
      nullable = false
  )
  private boolean darkMode;

  @Column(
      name = "active_calendar",
      nullable = false
  )
  private boolean activeCalendar;

  @Column(
      name = "priority_calendar",
      nullable = false
  )
  private int priorityCalendar;

  @Column(
      name = "active_dashboard",
      nullable = false
  )
  private boolean activeDashboard;

  @Column(
      name = "priority_dashboard",
      nullable = false
  )
  private int priorityDashboard;

  @Column(
      name = "active_stats",
      nullable = false
  )
  private boolean activeStats;

  @Column(
      name = "priority_stats",
      nullable = false
  )
  private int priorityStats;

  @Column(
      name = "realtime_sync_notion",
      nullable = false
  )
  private boolean realtimeSyncNotion;

  @Column(
      name = "realtime_sync_google_calendar",
      nullable = false
  )
  private boolean realtimeSyncGoogleCalendar;

  @Column(
      name = "realtime_sync_microsoft_todo",
      nullable = false
  )
  private boolean realtimeSyncMicrosoftTodo;

  public static UserEntity from(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .username(user.getUsername())
        .introduction(user.getIntroduction())
        .authority(user.getAuthority())
        .status(user.getStatus())
        .profileImageUrl(user.getProfileImageUrl())
        .allowingFollowsAfterApproval(user.isAllowingFollowsAfterApproval())
        .templateNotification(user.isNotifyingTemplate())
        .dduduNotification(user.isNotifyingDdudu())
        .weekStartDay(user.getWeekStartDay())
        .darkMode(user.isDarkMode())
        .activeCalendar(user.isActiveCalendar())
        .priorityCalendar(user.getPriorityCalendar())
        .activeDashboard(user.isActiveDashboard())
        .priorityDashboard(user.getPriorityDashboard())
        .activeStats(user.isActiveStats())
        .priorityStats(user.getPriorityStats())
        .realtimeSyncNotion(user.isRealtimeSyncNotion())
        .realtimeSyncGoogleCalendar(user.isRealtimeSyncGoogleCalendar())
        .realtimeSyncMicrosoftTodo(user.isRealtimeSyncMicrosoftTodo())
        .build();
  }

  public User toDomain() {
    return buildUser().build();
  }

  public User toDomainWith(List<AuthProvider> authProviders) {
    return buildUser()
        .authProviders(authProviders)
        .build();
  }

  private UserBuilder buildUser() {
    return User.builder()
        .id(id)
        .username(username)
        .nickname(nickname)
        .introduction(introduction)
        .authority(authority)
        .status(status)
        .profileImageUrl(profileImageUrl)
        .options(buildOptions());
  }

  private Options buildOptions() {
    return Options.builder()
        .allowingFollowsAfterApproval(allowingFollowsAfterApproval)
        .templateNotification(templateNotification)
        .dduduNotification(dduduNotification)
        .display(buildDisplayOptions())
        .menuActivation(buildMenuActivationOptions())
        .appConnection(buildAppConnectionOptions())
        .build();
  }

  private DisplayOptions buildDisplayOptions() {
    return DisplayOptions.builder()
        .weekStartDay(weekStartDay)
        .darkMode(darkMode)
        .build();
  }

  private MenuActivationOptions buildMenuActivationOptions() {
    return MenuActivationOptions.builder()
        .calendar(MenuActivationItem.builder()
            .active(activeCalendar)
            .priority(priorityCalendar)
            .build())
        .dashboard(MenuActivationItem.builder()
            .active(activeDashboard)
            .priority(priorityDashboard)
            .build())
        .stats(MenuActivationItem.builder()
            .active(activeStats)
            .priority(priorityStats)
            .build())
        .build();
  }

  private AppConnectionOptions buildAppConnectionOptions() {
    return AppConnectionOptions.builder()
        .realtimeSync(RealtimeSyncOptions.builder()
            .notion(realtimeSyncNotion)
            .googleCalendar(realtimeSyncGoogleCalendar)
            .microsoftTodo(realtimeSyncMicrosoftTodo)
            .build())
        .build();
  }

}
