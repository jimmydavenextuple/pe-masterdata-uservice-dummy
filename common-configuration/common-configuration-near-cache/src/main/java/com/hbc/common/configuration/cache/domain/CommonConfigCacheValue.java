package com.hbc.common.configuration.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommonConfigCacheValue extends AbstractCacheValue {

  private static final long serialVersionUID = -3498752828279642333L;
  private CommonConfigDetails commonConfigDetails;
}
