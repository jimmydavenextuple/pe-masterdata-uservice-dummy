package com.hbc.nodecarrier.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCarrierListCacheValue implements CacheValue {

  private List<NodeCarrierDetails> nodeCarrierDetailsList;

  @Override
  public boolean isUndefined() {
    return false;
  }
}
