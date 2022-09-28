package com.hbc.csvdownload.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransitDataUpload {

  private String orgId;

  private String sourceGeozone;

  private String destinationGeozone;

  private String carrierServiceId;

  private String transitDays;
}
