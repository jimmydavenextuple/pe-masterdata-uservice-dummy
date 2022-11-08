package com.nextuple.nodecarrier.cache.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCarrierDetails implements Serializable {

  private static final long serialVersionUID = -1528753386816961010L;

  private String nodeId;

  private String orgId;

  private String carrierServiceId;

  private String serviceOption;

  private Double processingTime;

  private String lastPickupTime;

  private Double bufferHours;

  private Date bufferStartDate;

  private Date bufferEndDate;
}
