package com.ddudu.old.user.dto.response;

import com.ddudu.application.domain.user.domain.Options;
import com.ddudu.application.domain.user.domain.User;
import lombok.Builder;

@Builder
public record ToggleOptionResponse(Long id, Boolean allowFollowsAfterApproval) {

  public static ToggleOptionResponse from(User user) {
    Options options = user.getOptions();

    return ToggleOptionResponse.builder()
        .id(user.getId())
        .allowFollowsAfterApproval(options.isAllowingFollowsAfterApproval())
        .build();
  }

}
