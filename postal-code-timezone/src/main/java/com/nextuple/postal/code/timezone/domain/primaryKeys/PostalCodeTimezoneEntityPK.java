package com.nextuple.postal.code.timezone.domain.primaryKeys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PostalCodeTimezoneEntityPK implements Serializable {
    private String orgId;
    private String postalCodePrefix;
}
