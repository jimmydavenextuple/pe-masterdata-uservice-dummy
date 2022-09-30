package com.hbc.dataupload.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeListDto implements Serializable {

  private static final long serialVersionUID = -9030089295880579769L;

  private String nodeId;
  private String orgId;
  private String nodeType;
  private String street;
  private String city;
  private String province;
  private String postalCode;
  private String latitude;
  private String longitude;
  private String timezone;
  private Boolean isActive;
  private List<NodeWorkingCalendarDto> nodeWorkingCalendar;
  private List<String> carrierServices;
  private List<String> serviceOptions;
  private List<PickupTimeDto> pickupTime;

}
