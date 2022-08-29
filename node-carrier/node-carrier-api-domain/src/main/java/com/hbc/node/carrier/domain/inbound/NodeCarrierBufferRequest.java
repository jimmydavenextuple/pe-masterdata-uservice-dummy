package com.hbc.node.carrier.domain.inbound;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierBufferRequest {

  @NotBlank(message = "nodeId cannot be empty")
  @Length(max = 50)
  private String nodeId;

  @NotBlank(message = "orgId cannot be empty")
  @Length(max = 50)
  private String orgId;

  @NotBlank(message = "serviceOption cannot be empty")
  @Length(max = 50)
  private String serviceOption;

  private Double bufferHours;

  private Date bufferStartDate;

  private Date bufferEndDate;
}
