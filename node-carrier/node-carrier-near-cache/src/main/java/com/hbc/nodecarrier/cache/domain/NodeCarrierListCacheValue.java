package com.hbc.nodecarrier.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class NodeCarrierListCacheValue implements CacheValue {

    private List<NodeCarrierDetails> nodeCarrierDetailsList;

}
