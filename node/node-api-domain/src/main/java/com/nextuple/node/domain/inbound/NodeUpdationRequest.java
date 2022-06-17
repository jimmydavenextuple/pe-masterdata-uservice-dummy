package com.nextuple.node.domain.inbound;

import java.io.Serializable;
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
