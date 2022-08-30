package com.hbc.node.domain.inbound;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeUpdationRequest implements Serializable {

  @NotBlank(message = "street can't be blank")
  private String street;

  @NotBlank(message = "timezone can't be blank")
  private String timezone;

  @NotBlank(message = "city can't be blank")
  private String city;

  @NotNull(message = "shipToHome can't be null")
  private Boolean shipToHome;

  @NotNull(message = "bopisEligible can't be null")
  private Boolean bopisEligible;

  @NotNull(message = "expressEligible can't be null")
  private Boolean expressEligible;

  @NotNull(message = "sdndEligible can't be null")
  private Boolean sdndEligible;

  @NotNull(message = "isActive can't be null")
  private Boolean isActive;

  @NotBlank(message = "nodeType can't be blank")
  private String nodeType;

  @NotBlank(message = "province can't be blank")
  private String province;

  @NotBlank(message = "country can't be blank")
  private String country;

  @NotBlank(message = "postalCode can't be blank")
  private String postalCode;

  @NotBlank(message = "latitude can't be blank")
  private String latitude;

  @NotBlank(message = "longitude can't be blank")
  private String longitude;
}
