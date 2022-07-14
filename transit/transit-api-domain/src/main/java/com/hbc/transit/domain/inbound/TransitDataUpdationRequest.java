package com.hbc.transit.domain.inbound;

import java.io.Serializable;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitDataUpdationRequest implements Serializable {

  @Min(value = 0, message = "transitDays can't be negative")
  private Float transitDays;
}
