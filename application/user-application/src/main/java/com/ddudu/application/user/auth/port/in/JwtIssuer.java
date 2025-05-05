package com.ddudu.application.user.auth.port.in;

import java.time.Duration;
import java.util.Map;

public interface JwtIssuer {

  String issue(Map<String, Object> claims, Duration expirationDuration);

}
