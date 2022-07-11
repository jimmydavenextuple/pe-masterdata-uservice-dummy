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
  private String country;
  private String latitude;
  private String longitude;
  private String province;
  private String postalCode;
  private String timezone;
  private Boolean shipToHome;
  private Boolean sdndEligible;
  private String nodeType;
  private Boolean isActive;
  private Boolean bopisEligible;
  private Boolean expressEligible;
}
