package com.ddudu.old.auth.domain.authority;

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
