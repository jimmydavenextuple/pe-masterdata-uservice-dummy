package com.nextuple.node.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCacheValue implements CacheValue {
  NodeDetails nodeDetails;
}
