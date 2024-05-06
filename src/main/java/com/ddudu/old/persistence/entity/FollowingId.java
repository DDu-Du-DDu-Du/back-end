package com.ddudu.old.persistence.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowingId implements Serializable {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", referencedColumnName = "id", nullable = false)
  private UserEntity follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "followee_id", referencedColumnName = "id", nullable = false)
  private UserEntity followee;

}
