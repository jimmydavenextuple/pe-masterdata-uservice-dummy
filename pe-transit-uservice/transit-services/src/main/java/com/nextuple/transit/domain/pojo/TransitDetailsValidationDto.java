package com.nextuple.transit.domain.pojo;

import com.nextuple.common.pojo.AdditionalAttributes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class TransitDetailsValidationDto extends AdditionalAttributes {

  private Float transitDays;

  private Double bufferDays;

  private String orgId;

  private String sourceGeozone;

  private String destinationGeozone;

  private String carrierServiceId;
}
