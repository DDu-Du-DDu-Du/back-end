package com.ddudu.friend.domain;

import com.ddudu.user.domain.User;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "followings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Following {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", referencedColumnName = "id")
  private User follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "followee_id", referencedColumnName = "id")
  private User followee;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private FriendStatus status;

  @Builder
  public Following(User follower, User followee, FriendStatus status) {
    this.follower = follower;
    this.followee = followee;
    this.status = status;
  }

}
