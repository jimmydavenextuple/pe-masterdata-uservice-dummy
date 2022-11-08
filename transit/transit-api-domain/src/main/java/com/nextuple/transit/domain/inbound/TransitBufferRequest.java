package com.nextuple.transit.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
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
public class TransitBufferRequest implements Serializable {
  private static final long serialVersionUID = -8905957802754295589L;

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 50)
  private String orgId;

  @NotBlank(message = "carrierServiceId can't be blank")
  @Length(max = 50)
  private String carrierServiceId;

  @NotBlank(message = "sourceGeozone can't be blank")
  @Length(max = 50)
  private String sourceGeozone;

  @NotBlank(message = "destinationGeozone can't be blank")
  @Length(max = 50)
  private String destinationGeozone;

  private Double bufferDays;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date bufferEndDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date bufferStartDate;

  private String createdBy;

  private String updatedBy;
}
