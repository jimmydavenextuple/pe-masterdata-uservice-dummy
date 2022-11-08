package com.nextuple.node.data.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeDataCacheKey implements CacheKey {
  public NodeDataCacheKey() {
    // default constructor
  }

  String nodeId;
  String orgId;
}
