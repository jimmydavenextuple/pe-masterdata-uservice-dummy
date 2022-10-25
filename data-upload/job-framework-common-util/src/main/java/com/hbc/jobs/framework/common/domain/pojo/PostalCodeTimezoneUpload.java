package com.hbc.jobs.framework.common.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostalCodeTimezoneUpload {

  private String orgId;
  private String postalCodePrefix;
  private String timeZone;
  private String country;
  private String city;
  private String state;
  private String longitude;
  private String latitude;
  private String action;
}
