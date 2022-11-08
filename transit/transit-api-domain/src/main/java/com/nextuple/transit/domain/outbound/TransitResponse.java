package com.nextuple.transit.domain.outbound;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitResponse implements Serializable {

  private String orgId;
  private String sourceGeozone;
  private String destinationGeozone;
  private String carrierServiceId;
  private Float transitDays;
  private Double bufferDays;
  private Date bufferStartDate;
  private Date bufferEndDate;
}
