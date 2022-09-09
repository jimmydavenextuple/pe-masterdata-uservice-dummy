package com.hbc.transit.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitBufferCreationRequest implements Serializable {

  @NotBlank(message = "sourceGeozone can't be blank")
  @Length(max = 50)
  private String sourceGeozone;

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 50)
  private String orgId;

  @NotBlank(message = "carrierServiceId can't be blank")
  @Length(max = 50)
  private String carrierServiceId;

  @NotBlank(message = "destinationGeozone can't be blank")
  @Length(max = 50)
  private String destinationGeozone;

  private Double bufferDays;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date bufferStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date bufferEndDate;
}
