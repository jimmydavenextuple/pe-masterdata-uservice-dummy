package com.hbc.node.carrier.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
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

  @NotNull(message = "carrierServiceId cannot be null")
  @Length(max = 50)
  private String carrierServiceId;

  @NotBlank(message = "serviceOption cannot be empty")
  @Length(max = 50)
  private String serviceOption;

  private Double processingTime;

  @Length(max = 50)
  private String lastPickupTime;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date bufferStartDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date bufferEndDate;

  private Double bufferHours;
}
