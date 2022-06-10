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
public class NodeId implements Serializable {

  private String nodeId;
  private String orgId;
}
