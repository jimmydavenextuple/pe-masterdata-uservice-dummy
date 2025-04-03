package com.nextuple.vendor.persistence.entity.key;

import com.nextuple.postgres.entity.key.BaseEntityKey;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorKey extends BaseEntityKey implements Serializable {
  private static final long serialVersionUID = -3796875055649660930L;
  private String vendorId;
  private String orgId;
}
