package com.nextuple.transit.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class TransitBufferConfigRequest implements Serializable {

  private static final long serialVersionUID = -1514262691280383765L;

  @Length(max = 50)
  @NotBlank(message = "orgId can't be empty")
  private String orgId;

  @Length(max = 50)
  @NotBlank(message = "carrierServiceId can't be empty")
  private String carrierServiceId;

  @NotNull private Double bufferDays;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  @NotNull
  private Date startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  @NotNull
  private Date endDate;

  private Long transitBufferRequestId;

  private String filePath;
  private String storageType;

  @NotBlank(message = "createdBy can't be blank")
  private String createdBy;

  private String action;
}
