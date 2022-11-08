package com.nextuple.dataupload.common.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransitBufferDetailsResponse {

  private String carrierServiceId;
  private String orgId;
  private boolean hasTransitBuffer;
}
