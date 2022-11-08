package com.nextuple.common.configuration.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommonConfigCacheKey implements CacheKey {

  private static final long serialVersionUID = 8785102075523906785L;
  private String orgId;

  private String type;

  private String key;

  public CommonConfigCacheKey() {
    // default constructor
  }
}
