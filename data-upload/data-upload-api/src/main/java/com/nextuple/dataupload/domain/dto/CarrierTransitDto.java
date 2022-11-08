package com.nextuple.dataupload.domain.dto;

import com.nextuple.dataupload.domain.pojo.CarrierServiceCalendars;
import java.io.Serializable;
import java.util.List;
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
  private List<CarrierServiceCalendars> carrierServiceCalendars;
}
