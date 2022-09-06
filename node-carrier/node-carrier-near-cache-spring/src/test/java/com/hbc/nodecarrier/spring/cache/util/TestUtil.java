package com.hbc.nodecarrier.spring.cache.util;

import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.hbc.nodecarrier.cache.domain.NodeCarrierDetails;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import java.util.List;

public class TestUtil {
  public static final String NODE_ID = "Node_Id_01";
  public static final String ORG_ID = "Org_Id_01";
  public static final String CARRIER_SERVICE_ID = "Carrier_Service_Id_01";
  public static final String SERVICE_OPTION = "Standard";
  public static final Double PROCESSING_TIME = 10.0;
  public static final String LAST_PICK_UP_TIME = "5:00 PM";

  public NodeCarrierCacheValue getNodeCarrierCacheValue() {
    NodeCarrierDetails nodeCarrierDetails = getNodeCarrierDetails();
    return NodeCarrierCacheValue.builder().nodeCarrierDetails(nodeCarrierDetails).build();
  }

  public NodeCarrierListCacheValue getNodeCarrierListCacheValue() {
    NodeCarrierDetails nodeCarrierDetails = getNodeCarrierDetails();
    return NodeCarrierListCacheValue.builder()
        .nodeCarrierDetailsList(List.of(nodeCarrierDetails))
        .build();
  }

  private NodeCarrierDetails getNodeCarrierDetails() {
    return NodeCarrierDetails.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(PROCESSING_TIME)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .build();
  }

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
        .payload(getNodeCarrierResponse())
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getListOfBaseResponseOfNodeCarrierResponse() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .payload(List.of(getNodeCarrierResponse()))
        .build();
  }

  public NodeCarrierCacheKey getNodeCarrierCacheKey() {
    return NodeCarrierCacheKey.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }

  public NodeCarrierListCacheKey getNodeCarrierListCacheKey() {
    return NodeCarrierListCacheKey.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }
}
