package com.nextuple.postal.code.timezone.api.domain.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class UpdatePostalCodeTimezoneRequest implements Serializable {
  private static final long serialVersionUID = 9006684261651646439L;

  @NotBlank(message = "country can't be empty.")
  private String country;

  @NotBlank(message = "state can't be empty.")
  private String state;

  @NotBlank(message = "city can't be empty.")
  private String city;

  @NotBlank(message = "latitude can't be empty.")
  private String latitude;

  @NotBlank(message = "longitude can't be empty.")
  private String longitude;

  @NotBlank(message = "timeZone can't be empty.")
  private String timeZone;
}
