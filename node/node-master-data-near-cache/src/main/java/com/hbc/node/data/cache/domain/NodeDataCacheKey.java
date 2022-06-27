package com.hbc.node.data.cache.domain;

import com.hbc.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeDataCacheKey implements CacheKey {
  public NodeDataCacheKey() {}

  String nodeId;
  String orgId;
}
