package com.nextuple.vendor.persistence.domain.key;

import com.nextuple.common.domain.key.DomainKey;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDomainKey extends DomainKey implements Serializable {
  private static final long serialVersionUID = -6933766705591251951L;
  private String vendorId;
  private String orgId;
}
