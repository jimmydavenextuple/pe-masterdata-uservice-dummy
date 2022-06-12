package com.nextuple.pe.masterdata.domain.primaryKeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NodeCarrierId implements Serializable {

  private String nodeId;
  private String orgId;
  private String carrierServiceId;
  private String serviceOption;
}
