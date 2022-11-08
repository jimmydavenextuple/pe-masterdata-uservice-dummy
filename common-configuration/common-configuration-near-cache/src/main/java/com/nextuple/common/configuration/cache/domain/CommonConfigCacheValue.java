package com.nextuple.common.configuration.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommonConfigCacheValue implements CacheValue {

  private static final long serialVersionUID = -3498752828279642333L;
  private CommonConfigDetails commonConfigDetails;
  private static final String UNDEFINED = "UNDEFINED";

  @Override
  public boolean isUndefined() {
    return UNDEFINED.equals(commonConfigDetails.getOrgId())
        && UNDEFINED.equals(commonConfigDetails.getType())
        && UNDEFINED.equals(commonConfigDetails.getKey())
        && UNDEFINED.equals(commonConfigDetails.getValue());
  }
}
