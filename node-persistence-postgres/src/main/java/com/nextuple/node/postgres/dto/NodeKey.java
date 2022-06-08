package com.nextuple.node.postgres.dto;

import com.nextuple.common.persistence.postgres.dto.key.PostgresEntityKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NodeKey implements PostgresEntityKey {
  public static final long serialVersionID = -2432345341L;
  private String nodeId;
  private String orgId;
}
