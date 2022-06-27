package com.hbc.common.dto;

import java.time.LocalDateTime;

public interface Entity {

  LocalDateTime getUpdateTime();

  String getUpdateUser();

  void setUpdateTime(LocalDateTime updateTime);

  void setUpdateUser(String updateUser);
}
