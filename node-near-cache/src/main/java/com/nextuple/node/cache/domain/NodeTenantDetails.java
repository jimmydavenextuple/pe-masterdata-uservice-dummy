package com.nextuple.node.cache.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeTenantDetails implements Serializable {
  private static final long serialVersionUID = -924965188677782223L;

  private String nodeNo;

  private String tenantId;
}
