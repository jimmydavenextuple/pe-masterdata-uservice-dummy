package com.hbc.jobs.framework.common.domain.pojo;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeDataUpload {

  private String action;
  private String nodeId;
  private String orgId;
  private String street;
  private String city;
  private String province;
  private String postalCode;
  private String country;
  private String latitude;
  private String longitude;
  private String timezone;
  private Map<String, Boolean> serviceOptionEligibilities;
  private String shipToHome;
  private String bopisEligible;
  private String nodeType;
  private String isActive;
}
