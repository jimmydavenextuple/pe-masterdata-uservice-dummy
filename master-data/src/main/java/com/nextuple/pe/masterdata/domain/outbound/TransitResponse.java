package com.nextuple.pe.masterdata.domain.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
}
