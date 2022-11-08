package com.nextuple.dataupload.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupTimeDto implements Serializable {

  private static final long serialVersionUID = -8987755286281473849L;

  private String nodeId;
  private String carrierServiceId;
  private String pickupTime;
}
