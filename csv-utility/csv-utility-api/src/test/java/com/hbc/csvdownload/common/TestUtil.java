package com.hbc.csvdownload.common;

import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {

  public static final String NODE_ID = "nodeId";
  public static final String ORG_ID = "orgId";
  public static final String SERVICE_OPTION = "serviceOptions";
  public static final Double PROCESSING_TIME = 20.0;
  public static final String leadProcessingTimesCsvData =
      "nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
          + "1554,BAY,SDND,2\n"
          + "1560,BAY,SDND,2\n"
          + "1101,BAY,SDND,2\n"
          + "1518,BAY,NEXTDAY,6\n"
          + "1634,BAY,EXPRESS,30.92\n"
          + "1601,BAY,EXPRESS,22.55\n"
          + "1125,BAY,EXPRESS,19.90\n"
          + "1114,BAY,SDND,24.97";

  public ProcessingLeadTimesRaw getProcessingLeadTimesRaw() {
    ProcessingLeadTimesRaw processingLeadTimesRaw = new ProcessingLeadTimesRaw();
    processingLeadTimesRaw.setNodeId(NODE_ID);
    processingLeadTimesRaw.setOrgId(ORG_ID);
    processingLeadTimesRaw.setServiceOption(SERVICE_OPTION);
    processingLeadTimesRaw.setProcessingTime(PROCESSING_TIME);

    return processingLeadTimesRaw;
  }

  public NodeCarrierRequest getNodeCarrierRequest() {
    NodeCarrierRequest nodeCarrierRequest = new NodeCarrierRequest();
    nodeCarrierRequest.setOrgId(ORG_ID);
    nodeCarrierRequest.setCarrierServiceId("ALL_SDND");
    nodeCarrierRequest.setLastPickupTime("00:00");
    nodeCarrierRequest.setNodeId(NODE_ID);
    nodeCarrierRequest.setProcessingTime(PROCESSING_TIME);
    nodeCarrierRequest.setServiceOption(SERVICE_OPTION);

    return nodeCarrierRequest;
  }
}
