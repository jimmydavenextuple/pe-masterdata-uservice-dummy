/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.common.pojo;

import org.springframework.stereotype.Service;

@Service
public class TemplateTypes {

  private TemplateTypes() {}

  private static final String TRANSIT_TIME_TEMPLATE =
      "orgId,BAY,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3,SGZ4,SGZ5,SGZ6,SGZ7,SGZ8,SGZ9,SGZ10\n"
          + "DGZ1,10,9.96,9.96,9.96,9.96,7,7,8.09,7,7\n"
          + "DGZ2,10,9,9,9,9,7.81,7.81,7.89,7.89,7.89\n"
          + "DGZ3,10,9,9,9,9,9.5,9.5,7,7.89,7.89\n"
          + "DGZ4,10,9.96,9.96,9.96,9.96,8.09,8.09,7.89,8.09,8.09\n"
          + "DGZ5,10,9.96,9.96,9.96,9.96,7.81,7.81,7.89,7.89,7.89\n"
          + "DGZ6,10,9.96,9.96,9.96,9.96,7,7,8.09,8.09,8.09\n"
          + "DGZ7,10,10,10,10,10,7.81,7.81,7.89,7.89,7.89\n"
          + "DGZ8,10,9.96,9.96,9.96,9.96,7.81,7.81,8.09,6,6\n"
          + "DGZ9,10,9.5,9.5,9.5,9.5,8.09,8.09,7.89,8.09,8.09\n"
          + "DGZ10,8,8,8,8,8,8.09,8.09,6,7,7";
  private static final String PROCESSING_LEAD_TIME =
      "action,nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
          + "UPDATE,1554,BAY,SDND,2\n"
          + "UPDATE,1560,BAY,SDND,2\n"
          + "UPDATE,1101,BAY,SDND,2\n"
          + "UPDATE,1634,BAY,EXPRESS,30.92\n"
          + "UPDATE,1601,BAY,EXPRESS,22.55\n"
          + "UPDATE,1114,BAY,SDND,24.97\n"
          + "DELETE,1518,BAY,NEXTDAY,6\n"
          + "DELETE,1125,BAY,EXPRESS,19.90";
  private static final String MARKET_REGION =
      "orgId,zipCodePrefix,country,state,city,latitude,longitude,timeZone\n"
          + "BAY,A0A,CA,NL,Witless Bay,47.545965,-53.138234,America/St_Johns\n"
          + "BAY,A0B,CA,NL,Brigus Junction,47.41876,-53.844888,America/St_Johns\n"
          + "BAY,A0C,CA,NL,Little Catalina,49.8279955,-54.4173245,America/St_Johns\n"
          + "BAY,A0E,CA,NL,Baine Harbour,47.45083,-54.73319,America/St_Johns\n"
          + "BAY,A0G,CA,NL,Baytona,49.1776,-54.442205,America/St_Johns\n"
          + "BAY,A0H,CA,NL,Badger,48.4864765,-54.7871645,America/St_Johns\n"
          + "BAY,A0J,CA,NL,Kings Point,49.355415,-55.5265845,America/St_Johns\n"
          + "BAY,A0K,CA,NL,Baie Verte,50.6864055,-56.432805,America/St_Johns";
  private static final String CARRIER_SERVICE =
      "orgId,carrierId,carrierName,carrierServiceId,serviceName,serviceOptions\n"
          + "BAY,GoFor,GoFor,GoFor-SDND,GoFor,SDND\n"
          + "BAY,TFORCE,TForce,TFORCE-NEXTDAY,TForce NextDay Guaranteed,NEXTDAY\n"
          + "BAY,CanadaPost,Canada Post,CanadaPost-STANDARD,Canada Post Expedited Parcel,STANDARD\n"
          + "BAY,CanadaPost,Canada Post,CanadaPost-EXPRESS,Canada Post Xpresspost,EXPRESS\n"
          + "BAY,UPSN,UPS,UPSN-STANDARD,UPS Standard,STANDARD\n";

  public static String getTemplateData(String templateType) {
    switch (templateType) {
      case "transitTime":
        return TRANSIT_TIME_TEMPLATE;
      case "processingLeadTime":
        return PROCESSING_LEAD_TIME;
      case "marketRegion":
        return MARKET_REGION;

      case "carrierService":
        return CARRIER_SERVICE;
      default:
        return "";
    }
  }
}
