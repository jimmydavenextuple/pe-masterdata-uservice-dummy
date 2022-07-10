package com.hbc.postal.code.timezone.api.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostalCodeTimezoneDto implements Serializable {
  private static final long serialVersionUID = -4258901633195345045L;
  private String orgId;
  private String postalCodePrefix;
  private String country;
  private String state;
  private String city;
  private String latitude;
  private String longitude;
  private String timeZone;
}
