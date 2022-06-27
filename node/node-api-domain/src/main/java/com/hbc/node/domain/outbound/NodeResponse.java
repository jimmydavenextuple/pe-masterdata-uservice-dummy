package com.hbc.node.domain.outbound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeResponse implements Serializable {

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
  private Boolean shipToHome;
  private Boolean sdndEligible;
  private Boolean bopisEligible;
  private Boolean expressEligible;
  private String nodeType;
  private Boolean isActive;
}
