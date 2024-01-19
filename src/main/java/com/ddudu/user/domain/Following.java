package com.ddudu.user.domain;

import com.ddudu.common.BaseEntity;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.user.exception.FollowingErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "followings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Following extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", referencedColumnName = "id", nullable = false)
  private User follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "followee_id", referencedColumnName = "id", nullable = false)
  private User followee;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private FollowingStatus status;

  @Builder
  public Following(User follower, User followee, FollowingStatus status) {
    validate(follower, followee);

    this.follower = follower;
    this.followee = followee;
    this.status = Objects.requireNonNullElse(status, FollowingStatus.FOLLOWING);
  }

  public boolean isRequestedTo(User user) {
    return  Objects.equals(followee, user);
  }

  public boolean isOwnedBy(User owner) {
    return Objects.equals(follower, owner);
  }

  public void updateStatus(FollowingStatus status) {
    if (Objects.isNull(status)) {
      throw new InvalidParameterException(FollowingErrorCode.NULL_STATUS_REQUESTED);
    }

    if (!status.isModifiable()) {
      throw new InvalidParameterException(FollowingErrorCode.REQUEST_UNAVAILABLE);
    }

    this.status = status;
  }

  private void validate(User follower, User followee) {
    if (Objects.isNull(follower)) {
      throw new InvalidParameterException(FollowingErrorCode.NULL_FOLLOWER);
    }

    if (Objects.isNull(followee)) {
      throw new InvalidParameterException(FollowingErrorCode.NULL_FOLLOWEE);
    }

    if (follower.equals(followee)) {
      throw new InvalidParameterException(FollowingErrorCode.SELF_FOLLOWING_UNAVAILABLE);
    }
  }

}
