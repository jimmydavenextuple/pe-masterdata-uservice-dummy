package com.hbc.node.carrier.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
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

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date bufferStartDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date bufferEndDate;
}
