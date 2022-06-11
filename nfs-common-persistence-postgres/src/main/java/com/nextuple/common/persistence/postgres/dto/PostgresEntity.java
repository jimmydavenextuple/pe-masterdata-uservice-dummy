package com.nextuple.common.persistence.postgres.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface PostgresEntity extends Serializable {

  LocalDateTime getUpdateTime();

  String getUpdateUser();

  void setUpdateTime(LocalDateTime updateTime);

  void setUpdateUser(String updateUser);
}
