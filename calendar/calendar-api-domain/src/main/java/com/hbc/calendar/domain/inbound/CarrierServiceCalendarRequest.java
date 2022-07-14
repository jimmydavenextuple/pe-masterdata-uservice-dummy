package com.hbc.calendar.domain.inbound;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierServiceCalendarRequest {

  @NotBlank(message = "calendarId can't be blank")
  @Length(max = 40)
  private String calendarId;

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 40)
  private String orgId;

  @NotBlank(message = "carrierServiceId can't be blank")
  @Length(max = 40)
  private String carrierServiceId;

  @NotBlank(message = "shippingStage can't be blank")
  private String shippingStage;

  @NotBlank(message = "effectiveDate can't be blank")
  private String effectiveDate;

  private String description;
}
