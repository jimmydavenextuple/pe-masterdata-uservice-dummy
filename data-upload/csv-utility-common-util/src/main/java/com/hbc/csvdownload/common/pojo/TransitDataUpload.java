package com.hbc.csvdownload.common.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransitDataUpload {
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

  @Min(value = 0, message = "transitDays can't be negative")
  @NotNull(message = "transitDays can't be null")
  private Float transitDays;

  private String actionType;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date bufferStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date bufferEndDate;

  private Double bufferDays;
}
