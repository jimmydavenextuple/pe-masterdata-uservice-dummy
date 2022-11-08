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
public class TransitBufferResponse implements Serializable {
  private static final long serialVersionUID = -8267269941227302889L;

  private String orgId;

  private String carrierServiceId;

  private String sourceGeozone;

  private String destinationGeozone;

  private Double bufferDays;

  private Date bufferStartDate;

  private Date bufferEndDate;

  private String createdBy;

  private String updatedBy;

  private Date createdDate;

  private Date lastModifiedDate;
}
