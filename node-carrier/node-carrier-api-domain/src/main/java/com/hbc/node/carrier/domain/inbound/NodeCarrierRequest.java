package com.hbc.node.carrier.domain.inbound;

import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierRequest implements Serializable {

  private static final long serialVersionUID = -5879215809620272913L;

  @NotBlank(message = "nodeId cannot be empty")
  @Length(max = 50)
  private String nodeId;

  @NotBlank(message = "orgId cannot be empty")
  @Length(max = 50)
  private String orgId;

  @NotBlank(message = "carrierServiceId cannot be empty")
  @Length(max = 50)
  private String carrierServiceId;

  @NotBlank(message = "serviceOption cannot be empty")
  @Length(max = 50)
  private String serviceOption;

  @NotNull(message = "processingTime cannot be null")
  @Min(value = 0, message = "processingTime can't be negative")
  private Double processingTime;

  @NotBlank(message = "lastPickupTime cannot be empty")
  @Length(max = 50)
  private String lastPickupTime;

  private Date bufferStartDate;

  private Date bufferEndDate;

  private Double bufferHours;
}
