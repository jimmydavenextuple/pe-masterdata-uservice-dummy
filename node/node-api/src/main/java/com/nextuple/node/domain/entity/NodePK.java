package com.nextuple.node.domain.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NodePK implements Serializable {

  private String nodeId;
  private String orgId;
}
