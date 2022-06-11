package com.nextuple.core.base;

import java.util.Date;
import lombok.Data;

@Data
public class BaseEvent {
  private String eventName;
  private Date eventTimestamp;
  private String originModule;
  private String eventModule;
}
