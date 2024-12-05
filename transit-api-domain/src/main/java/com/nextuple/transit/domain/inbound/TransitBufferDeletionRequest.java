package com.nextuple.transit.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitBufferDeletionRequest implements Serializable {

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
  private String carrierServiceId;

  @Schema(description = "Source geo zone of the transit.", example = "A1B")
  private String sourceGeozone;

  @Schema(description = "Destination geo zone of the transit.", example = "H1R")
  private String destinationGeozone;

  @Schema(description = "End date of the transit buffer.", example = "2023-12-26T11:58:22Z")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date bufferEndDate;

  @Schema(description = "Start date of the transit buffer.", example = "2023-12-24T11:58:22Z")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date bufferStartDate;
}
