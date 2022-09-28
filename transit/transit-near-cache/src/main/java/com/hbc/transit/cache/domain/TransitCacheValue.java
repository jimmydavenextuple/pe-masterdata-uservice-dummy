package com.hbc.transit.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TransitCacheValue implements CacheValue {
  private List<TransitResponse> transitResponseList;

  @Override
  public boolean isUndefined() {
    return false;
  }
}
