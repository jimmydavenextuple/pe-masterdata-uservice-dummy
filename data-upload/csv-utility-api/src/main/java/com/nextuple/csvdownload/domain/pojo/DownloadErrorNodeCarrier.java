package com.nextuple.csvdownload.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadErrorNodeCarrier {

  private String nodeId;

  private String orgId;

  private String carrierServiceId;

  private String serviceOption;

  private String processingTime;

  private String lastPickupTime;

  private String bufferStartDate;

  private String bufferEndDate;

  private String bufferHours;
}
