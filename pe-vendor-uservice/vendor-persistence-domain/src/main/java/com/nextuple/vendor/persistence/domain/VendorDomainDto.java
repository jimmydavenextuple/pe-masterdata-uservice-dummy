package com.nextuple.vendor.persistence.domain;

import com.nextuple.common.domain.DomainBaseEntity;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDomainDto extends DomainBaseEntity implements Serializable {
  String vendorId;
  String orgId;
  String vendorDescription;
  String vendorType;
}
