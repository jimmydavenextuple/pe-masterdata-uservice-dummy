package com.hbc.nodecarrier.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCarrierCacheValue implements CacheValue {

  private static final long serialVersionUID = 2012829361093199932L;

  private NodeCarrierDetails nodeCarrierDetails;

  @Override
  public boolean isUndefined() {
    return false;
  }
}
