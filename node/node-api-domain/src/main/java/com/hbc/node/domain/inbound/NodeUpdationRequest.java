package com.hbc.node.domain.inbound;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeUpdationRequest implements Serializable {

  private String street;

  private String city;

  private String timezone;

  private Map<String, Boolean> serviceOptionEligibilities;

  private Boolean shipToHome;

  private Boolean bopisEligible;

  private String nodeType;

  private String province;

  private String postalCode;

  private String country;

  private String latitude;

  private String longitude;

  private Boolean isActive;
}
