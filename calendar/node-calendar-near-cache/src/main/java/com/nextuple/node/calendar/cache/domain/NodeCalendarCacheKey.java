package com.nextuple.node.calendar.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCalendarCacheKey implements CacheKey {
  private static final long serialVersionUID = 4024017030652933137L;
  private String nodeId;
  private String orgId;

  public NodeCalendarCacheKey() {
    // constructor
  }
}
