package com.hbc.node.carrier.calendar.cache.domain;

import com.hbc.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCarrierCalendarCacheKey implements CacheKey {
  private static final long serialVersionUID = 4024017030652933137L;
  private String nodeId;
  private String carrierServiceId;
  private String orgId;
  private String serviceOption;

  public NodeCarrierCalendarCacheKey() {}
}
