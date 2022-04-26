package com.nextuple.node.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCacheKey implements CacheKey {
  //  String nodeNo;
  NodeTenantDetails nodeTenantDetails;
}
