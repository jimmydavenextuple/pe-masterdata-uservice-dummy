package com.nextuple.pe.masterdata.domain.primaryKeys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServiceToInventoryId implements Serializable {
  private String orgId;
  private String serviceOption;
}
