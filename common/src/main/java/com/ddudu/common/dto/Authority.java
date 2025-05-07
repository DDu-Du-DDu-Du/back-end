package com.ddudu.common.dto;

public enum Authority {
  ADMIN,
  NORMAL,
  GUEST;

  public String getAuthority() {
    return this.name();
  }

}
