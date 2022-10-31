package com.hbc.fsa.node.pojo;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeDetails {
  private String nodeId;
  private String orgId;
  private String street;
  private String nodeType;
  private Boolean isActive;
  private String city;
  private String province;
  private String postalCode;
  private String country;
  private String latitude;
  private Map<String, Boolean> serviceOptionEligibilities;
  private Boolean shipToHome;
  private Boolean bopisEligible;
  private String longitude;
  private String timezone;
}
