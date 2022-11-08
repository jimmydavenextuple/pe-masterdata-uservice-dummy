package com.nextuple.nodecarrier.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCarrierListCacheKey implements CacheKey {

  private String nodeId;

  private String orgId;

  private String serviceOption;

  public NodeCarrierListCacheKey() {
    // default constructor
  }
}
