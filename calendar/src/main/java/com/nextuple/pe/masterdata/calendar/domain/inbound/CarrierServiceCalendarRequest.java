package com.nextuple.pe.masterdata.calendar.domain.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierServiceCalendarRequest {

  @NotBlank(message = "calendarId cannot be blank")
  @Length(max = 40)
  private String calendarId;

  @NotBlank(message = "orgId cannot be blank")
  @Length(max = 40)
  private String orgId;

  @NotBlank(message = "carrierServiceId cannot be blank")
  @Length(max = 40)
  private String carrierServiceId;

  @NotBlank(message = "shippingStage cannot be blank")
  private String shippingStage;

  @NotBlank(message = "effectiveDate cannot be blank")
  private String effectiveDate;

  private String description;
}
