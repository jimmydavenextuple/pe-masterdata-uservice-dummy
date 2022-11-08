package com.nextuple.dataupload.domain.dto;

import com.nextuple.dataupload.domain.pojo.PickUpCalendar;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeCarrierServiceResponse {

  private String nodeId;
  private String orgId;
  private String street;
  private String city;
  private String province;
  private String postalCode;
  private List<String> carrierServices;
  private List<PickUpCalendar> pickupCalendar;
}
