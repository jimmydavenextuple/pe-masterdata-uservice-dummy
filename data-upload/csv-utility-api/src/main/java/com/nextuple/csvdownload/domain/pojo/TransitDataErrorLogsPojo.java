package com.nextuple.csvdownload.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransitDataErrorLogsPojo {

  private String orgId;
  private String sourceGeozone;
  private String destinationGeozone;
  private String carrierServiceId;
  private String transitDays;
  private String errorMessage;
  private String exception;
}
