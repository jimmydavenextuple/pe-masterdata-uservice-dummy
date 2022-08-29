package com.hbc.csvdownload.common;

import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {

  public static final String NODE_ID = "nodeId";
  public static final String ORG_ID = "orgId";
  public static final String SERVICE_OPTION = "serviceOptions";
  public static final Double PROCESSING_TIME = 20.0;
  public static final String processingLeadTimesCsvData =
      "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
          + "1554,BAY,SDND,2,U\n"
          + "1560,BAY,SDND,2,U\n"
          + "1101,BAY,SDND,2,U\n"
          + "1518,BAY,NEXTDAY,6,D\n"
          + "1634,BAY,EXPRESS,30.92,U\n"
          + "1601,BAY,EXPRESS,22.55,U\n"
          + "1125,BAY,EXPRESS,19.90,D\n"
          + "1114,BAY,SDND,24.97,U";

  public ProcessingLeadTimesRaw getProcessingLeadTimesRaw() {
    ProcessingLeadTimesRaw processingLeadTimesRaw = new ProcessingLeadTimesRaw();
    processingLeadTimesRaw.setNodeId(NODE_ID);
    processingLeadTimesRaw.setOrgId(ORG_ID);
    processingLeadTimesRaw.setServiceOption(SERVICE_OPTION);
    processingLeadTimesRaw.setProcessingTime(PROCESSING_TIME);

    return processingLeadTimesRaw;
  }
}
