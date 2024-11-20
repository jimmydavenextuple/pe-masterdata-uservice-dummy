package com.nextuple.transit.domain.pojo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TransitBufferValidationDto {
  private String orgId;

  private String carrierServiceId;

  private String sourceGeozone;

  private String destinationGeozone;

  private Date bufferEndDate;

  private Date bufferStartDate;
}
