package com.hbc.dataupload.common.outbound;

import com.hbc.dataupload.common.pojo.ActiveCombination;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierServiceAndServiceOptionResponse {

  private String nodeId;
  private String orgId;
  private String street;
  private String city;
  private String province;
  private String postalCode;
  private List<String> carrierServices;
  private List<String> serviceOptions;
  private List<ActiveCombination> activeCombination;
}
