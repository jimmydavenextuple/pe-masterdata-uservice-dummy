package com.nextuple.transit.domain.pojo;

import com.nextuple.common.pojo.AdditionalAttributes;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class TransitBufferValidationDto extends AdditionalAttributes {
  private String orgId;

  private String carrierServiceId;

  private String sourceGeozone;

  private String destinationGeozone;

  private Date bufferEndDate;

  private Date bufferStartDate;
}
