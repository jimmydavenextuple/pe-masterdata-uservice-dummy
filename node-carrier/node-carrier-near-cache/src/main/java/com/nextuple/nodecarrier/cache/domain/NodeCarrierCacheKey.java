package com.nextuple.nodecarrier.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCarrierCacheKey implements CacheKey {

  private static final long serialVersionUID = 3750384489654713419L;

  private String nodeId;

  private String orgId;

  private String carrierServiceId;

  private String serviceOption;

  public NodeCarrierCacheKey() {
    // default constructor
  }
}
