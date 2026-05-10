package com.modoo.domain.user.user.aggregate.enums;

import static com.google.common.base.Preconditions.checkArgument;

import com.modoo.common.exception.UserErrorCode;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public enum Language {
  KO,
  EN;

  public static Language get(String input) {
    checkArgument(
        StringUtils.isNotBlank(input),
        UserErrorCode.INVALID_LANGUAGE.getCodeName()
    );

    String upperCased = input.toUpperCase(Locale.ROOT);

    return Arrays.stream(values())
        .filter(language -> language.name()
            .equals(upperCased))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException(
            UserErrorCode.INVALID_LANGUAGE.getCodeName()
        ));
  }

}
