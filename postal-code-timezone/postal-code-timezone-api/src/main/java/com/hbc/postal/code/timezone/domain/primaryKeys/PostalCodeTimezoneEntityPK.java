package com.hbc.postal.code.timezone.domain.primaryKeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PostalCodeTimezoneEntityPK implements Serializable {
  private String orgId;
  private String postalCodePrefix;
}
