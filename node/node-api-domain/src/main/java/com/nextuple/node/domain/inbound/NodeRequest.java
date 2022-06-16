package com.nextuple.node.domain.inbound;

import java.io.Serializable;
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

  @NotBlank
  @Length(max = 50)
  private String nodeId;

  @NotBlank
  @Length(max = 50)
  private String orgId;

  @NotBlank
  @Length(max = 50)
  private String street;

  @NotBlank
  @Length(max = 50)
  private String city;

  @NotBlank
  @Length(max = 50)
  private String province;

  @NotBlank
  @Length(max = 50)
  private String postalCode;

  @Length(max = 50)
  private String country;

  @NotBlank
  @Length(max = 50)
  private String latitude;

  @NotBlank
  @Length(max = 50)
  private String longitude;

  @NotBlank
  @Length(max = 50)
  private String timezone;

  @NotNull private Boolean shipToHome;

  @NotNull private Boolean sdndEligible;

  @NotNull private Boolean bopisEligible;

  @NotNull private Boolean expressEligible;

  @NotBlank
  @Length(max = 50)
  private String nodeType;

  @NotNull private Boolean isActive;
}
