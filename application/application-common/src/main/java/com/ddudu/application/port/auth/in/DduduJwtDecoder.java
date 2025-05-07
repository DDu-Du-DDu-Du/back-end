package com.ddudu.application.port.auth.in;

public interface DduduJwtDecoder {

  String getSub(String token);

}
