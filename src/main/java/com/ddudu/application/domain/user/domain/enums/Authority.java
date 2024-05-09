package com.ddudu.application.domain.user.domain.enums;

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
