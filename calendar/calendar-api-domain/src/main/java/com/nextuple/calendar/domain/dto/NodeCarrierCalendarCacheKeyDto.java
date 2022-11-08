package com.nextuple.calendar.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeCarrierCalendarCacheKeyDto implements Serializable {
  private static final long serialVersionUID = 3853978461669238791L;

  private String nodeId;
  private String carrierServiceId;
  private String orgId;
  private String serviceOption;
}
