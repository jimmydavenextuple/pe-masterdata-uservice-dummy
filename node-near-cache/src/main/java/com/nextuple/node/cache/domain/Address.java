package com.nextuple.node.cache.domain;

import com.azure.core.models.GeoPoint;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address implements Serializable {

  private static final long serialVersionUID = 6843619663343248331L;
  private String addressLineOne;
  private String addressLineTwo;

  @NotNull(message = "{city.notnull.msg}")
  private String city;

  @NotNull(message = "{state.notnull.msg}")
  private String state;

  @NotNull(message = "{zipCode.notnull.msg}")
  private String zipCode;

  @NotNull(message = "{country.notnull.msg}")
  private String country;

  @NotNull(message = "{timeZone.notnull.msg}")
  private String timeZone;

  @Valid private GeoPoint geoPoint;
}
