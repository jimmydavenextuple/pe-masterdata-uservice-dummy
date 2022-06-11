package com.nextuple.nodecarrier.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCarrierCacheValue implements CacheValue {

  private static final long serialVersionUID = 2012829361093199932L;

  private NodeCarrierDetails nodeCarrierDetails;
}
