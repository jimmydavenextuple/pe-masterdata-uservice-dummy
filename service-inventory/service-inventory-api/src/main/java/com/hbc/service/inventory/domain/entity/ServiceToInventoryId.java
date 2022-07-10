package com.hbc.service.inventory.domain.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServiceToInventoryId implements Serializable {
  private String orgId;
  private String serviceOption;
}
