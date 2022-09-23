package com.hbc.csvdownload.common.pojo;

import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransitDataUpload extends TransitDataCreationRequest {
  private String actionType;
}
