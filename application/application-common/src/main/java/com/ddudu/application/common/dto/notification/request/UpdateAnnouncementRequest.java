package com.ddudu.application.common.dto.notification.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAnnouncementRequest(
    @NotBlank(message = "13001 NULL_TITLE")
    @Size(max = 50, message = "13002 EXCESSIVE_TITLE_LENGTH")
    String title,
    @NotBlank(message = "13003 NULL_CONTENTS")
    @Size(max = 2000, message = "13004 EXCESSIVE_CONTENTS_LENGTH")
    String body
) {

}
