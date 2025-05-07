package com.ddudu.application.port.auth.in;

import java.time.Duration;
import java.util.Map;

public interface JwtIssuer {

  String issue(Map<String, Object> claims, Duration expirationDuration);

  Map<String, Object> setSub(Map<String, Object> claim, Object sub);

}
