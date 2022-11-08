package com.nextuple.node.data.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import java.util.Map;
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
  private String nodeType;
  private Boolean isActive;
  private Map<String, Boolean> serviceOptionEligibilities;
  private Boolean shipToHome;
  private Boolean bopisEligible;

  @Override
  public boolean isUndefined() {
    return false;
  }
}
