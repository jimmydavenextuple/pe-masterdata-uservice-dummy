package com.hbc.postal.code.timezone.api.domain.inbound;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreatePostalCodeTimezoneRequest implements Serializable {
  private static final long serialVersionUID = 768362509382042475L;

  @NotBlank(message = "orgId can't be blank")
  private String orgId;

  @NotBlank(message = "postalCodePrefix can't be blank")
  private String postalCodePrefix;

  @NotBlank(message = "country can't be blank")
  private String country;

  @NotBlank(message = "state can't be blank")
  private String state;

  @NotBlank(message = "city can't be blank")
  private String city;

  @NotBlank(message = "latitude can't be blank")
  private String latitude;

  @NotBlank(message = "longitude can't be blank")
  private String longitude;

  @NotBlank(message = "timeZone can't be blank")
  private String timeZone;
}
