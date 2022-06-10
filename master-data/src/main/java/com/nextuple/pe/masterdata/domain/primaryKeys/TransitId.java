package com.nextuple.pe.masterdata.domain.primaryKeys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransitId implements Serializable {

  private String orgId;
  private String sourceGeozone;
  private String destinationGeozone;
  private String carrierServiceId;
}
