package com.hbc.csvdownload.common.pojo;

import org.springframework.stereotype.Service;

@Service
public class TemplateTypes {

  private TemplateTypes() {}

  private static final String TRANSIT_TIME_TEMPLATE =
      "orgId,BAY,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3,SFSA4,SFSA5,SFSA6,SFSA7,SFSA8,SFSA9,SFSA10\n"
          + "DFSA1,10,9.96,9.96,9.96,9.96,7,7,8.09,7,7\n"
          + "DFSA2,10,9,9,9,9,7.81,7.81,7.89,7.89,7.89\n"
          + "DFSA3,10,9,9,9,9,9.5,9.5,7,7.89,7.89\n"
          + "DFSA4,10,9.96,9.96,9.96,9.96,8.09,8.09,7.89,8.09,8.09\n"
          + "DFSA5,10,9.96,9.96,9.96,9.96,7.81,7.81,7.89,7.89,7.89\n"
          + "DFSA6,10,9.96,9.96,9.96,9.96,7,7,8.09,8.09,8.09\n"
          + "DFSA7,10,10,10,10,10,7.81,7.81,7.89,7.89,7.89\n"
          + "DFSA8,10,9.96,9.96,9.96,9.96,7.81,7.81,8.09,6,6\n"
          + "DFSA9,10,9.5,9.5,9.5,9.5,8.09,8.09,7.89,8.09,8.09\n"
          + "DFSA10,8,8,8,8,8,8.09,8.09,6,7,7";
  private static final String PROCESSING_LEAD_TIME =
      "nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
          + "1554,BAY,SDND,2\n"
          + "1560,BAY,SDND,2\n"
          + "1101,BAY,SDND,2\n"
          + "1518,BAY,NEXTDAY,6\n"
          + "1634,BAY,EXPRESS,30.92\n"
          + "1601,BAY,EXPRESS,22.55\n"
          + "1125,BAY,EXPRESS,19.90\n"
          + "1114,BAY,SDND,24.97";

  public static String getTemplateData(String templateType) {
    switch (templateType) {
      case "transitTime":
        return TRANSIT_TIME_TEMPLATE;
      case "processingLeadTime":
        return PROCESSING_LEAD_TIME;
      default:
        return "";
    }
  }
}
