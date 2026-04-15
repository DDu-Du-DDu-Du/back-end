package com.modoo.application.common.port.auth.in;

public interface LogoutUseCase {

  void logout(Long loginUserId, String refreshToken);

}
