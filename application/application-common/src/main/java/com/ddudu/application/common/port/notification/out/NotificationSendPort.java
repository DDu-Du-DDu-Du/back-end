package com.ddudu.application.common.port.notification.out;

import java.util.List;

public interface NotificationSendPort {

  void sendToDevices(List<String> deviceTokens, String title, String body);

}
