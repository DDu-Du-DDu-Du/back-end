package com.ddudu.api.user.auth.jwt;

import com.ddudu.common.dto.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum AuthorityProxy implements GrantedAuthority {

  ADMIN(Authority.ADMIN),
  NORMAL(Authority.NORMAL),
  GUEST(Authority.GUEST),
  ;

  private final Authority authority;

  @Override
  public String getAuthority() {
    return authority.getAuthority();
  }
}
