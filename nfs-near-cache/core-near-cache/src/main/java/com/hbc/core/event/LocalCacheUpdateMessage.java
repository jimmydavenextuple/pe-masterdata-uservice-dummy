package com.hbc.core.event;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalCacheUpdateMessage implements Serializable {
  private static final long serialVersionUID = 8371831680896854461L;

  private Map<String, Object> message;
  private String entityName;
}
