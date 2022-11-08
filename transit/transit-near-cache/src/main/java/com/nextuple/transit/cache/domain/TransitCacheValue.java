package com.nextuple.transit.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.transit.domain.outbound.TransitResponse;
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
