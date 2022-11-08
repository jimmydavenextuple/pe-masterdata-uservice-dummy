package com.nextuple.postal.code.timezone.domain.primarykeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostalCodeTimezoneEntityPK implements Serializable {
  private String orgId;
  private String postalCodePrefix;
}
