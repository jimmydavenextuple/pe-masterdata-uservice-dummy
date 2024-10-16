package com.nextuple.promise.sourcing.rule.api.domain.projection;

public interface SourcingRuleByNodeGroupCountProjection {
  String getNodeGroup();

  Long getCount();

  void setNodeGroup(String nodeGroup);

  void setCount(Long count);
}
