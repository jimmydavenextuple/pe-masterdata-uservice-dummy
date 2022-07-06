package com.hbc.nodecarrier.data.upload.utility.util;

import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;

public class TestUtil {
  public static final String NODE_ID = "Node_Id_01";
  public static final String ORG_ID = "Org_Id_01";
  public static final String CARRIER_SERVICE_ID = "Carrier_Service_Id_01";
  public static final String SERVICE_OPTION = "Standard";
  public static final Double PROCESSING_TIME = 10.0;
  public static final String LAST_PICK_UP_TIME = "5:00 PM";

  private NodeCarrierResponse getNodeCarrierResponse() {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(PROCESSING_TIME)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .build();
  }

  public BaseResponse<NodeCarrierResponse> getBaseResponseOfNodeCarrierResponse() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .success(true)
        .payload(getNodeCarrierResponse())
        .build();
  }
}
