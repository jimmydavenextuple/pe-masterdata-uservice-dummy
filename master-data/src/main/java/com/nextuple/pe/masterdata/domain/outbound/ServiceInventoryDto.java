package com.nextuple.pe.masterdata.domain.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceInventoryDto implements Serializable {
  private String orgId;
  private String serviceOption;
  private String inventoryType;
}
