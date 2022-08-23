package com.hbc.node.domain.inbound;

import java.io.Serializable;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeRequest implements Serializable {

  @NotBlank(message = "nodeId can't be blank")
  @Length(max = 50)
  private String nodeId;

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 50)
  private String orgId;

  @NotBlank(message = "street can't be blank")
  @Length(max = 50)
  private String street;

  @NotBlank(message = "city can't be blank")
  @Length(max = 50)
  private String city;

  @NotBlank(message = "province can't be blank")
  @Length(max = 50)
  private String province;

  @NotBlank(message = "postalCode can't be blank")
  @Length(max = 50)
  private String postalCode;

  @NotBlank(message = "country can't be blank")
  @Length(max = 50)
  private String country;

  @NotBlank(message = "latitude can't be blank")
  @Length(max = 50)
  private String latitude;

  @NotBlank(message = "longitude can't be blank")
  @Length(max = 50)
  private String longitude;

  @NotBlank(message = "timezone can't be blank")
  @Length(max = 50)
  private String timezone;

  @NotNull(message = "serviceOptionEligibilities can't be null")
  private Map<String, Boolean> serviceOptionEligibilities;

  @NotNull(message = "shipToHome can't be null")
  private Boolean shipToHome;

  @NotNull(message = "bopisEligible can't be null")
  private Boolean bopisEligible;

  @NotBlank(message = "nodeType can't be blank")
  @Length(max = 50)
  private String nodeType;

  @NotNull(message = "isActive can't be null")
  private Boolean isActive;
}
