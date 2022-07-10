package com.hbc.node.data.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeDataCacheValue implements CacheValue {
  private String nodeId;
  private String orgId;
  private String street;
  private String city;
  private String latitude;
  private String longitude;
  private String timezone;
  private String province;
  private String postalCode;
  private String country;
  private Boolean expressEligible;
  private String nodeType;
  private Boolean isActive;
  private Boolean shipToHome;
  private Boolean sdndEligible;
  private Boolean bopisEligible;
}
