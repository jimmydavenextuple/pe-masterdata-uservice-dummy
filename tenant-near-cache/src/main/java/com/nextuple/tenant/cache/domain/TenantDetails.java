package com.nextuple.tenant.cache.domain;

import com.nextuple.core.tenant.enums.TenantStatusEnum;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantDetails implements Serializable {
  private static final long serialVersionUID = -9164965188635372223L;
  private String tenantId;
  private String tenantName;
  private TenantStatusEnum status;
}
