package com.ddudu.domain.user.user.aggregate.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
  ADMIN,
  NORMAL,
  GUEST;

  @Override
  public String getAuthority() {
    return this.name();
  }

}
