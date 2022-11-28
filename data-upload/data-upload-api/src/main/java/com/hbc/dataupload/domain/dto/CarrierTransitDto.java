package com.hbc.dataupload.domain.dto;

import com.hbc.dataupload.domain.pojo.CarrierServiceCalendar;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrierTransitDto implements Serializable {
  private static final long serialVersionUID = 8525520944139125658L;

  private String orgId;
  private String carrierId;
  private String carrierServiceId;
  private String carrierName;
  private String serviceName;
  private Boolean isCarrierActive;
  private Boolean isCalendarAssigned;
  private CarrierServiceCalendar carrierServiceCalendar;
}
