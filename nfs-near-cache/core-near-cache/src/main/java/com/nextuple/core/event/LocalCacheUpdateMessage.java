package com.nextuple.core.event;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalCacheUpdateMessage {

  Map<String, String> message;
  String entityName;
}
