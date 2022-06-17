package com.nextuple.node.carrier.domain.inbound;

import java.io.Serializable;
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
public class NodeCarrierUpdateRequest implements Serializable {

  private static final long serialVersionUID = 708117169845359905L;

  @NotNull(message = "processingTime cannot be null")
  private Double processingTime;

  @NotBlank(message = "lastPickupTime cannot be empty")
  @Length(max = 50)
  private String lastPickupTime;
}
