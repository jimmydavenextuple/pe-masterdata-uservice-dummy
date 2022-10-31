package com.hbc.fsa.node.pojo;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeFSAMessage {
  private String eventTime;
  private String eventName;
  private String eventTrigger;
  private String orgId;
  private String nodeId;
  private String type;
  private Map<String, FSACoverage> fsaCoverage;
  private Address address;
  private Map<String, Boolean> nodeEligibilityFlags;
}
