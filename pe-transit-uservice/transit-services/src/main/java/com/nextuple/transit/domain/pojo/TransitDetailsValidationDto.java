package com.nextuple.transit.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TransitDetailsValidationDto {

  private Float transitDays;

  private Double bufferDays;

  private String orgId;

  private String sourceGeozone;

  private String destinationGeozone;

  private String carrierServiceId;
}
