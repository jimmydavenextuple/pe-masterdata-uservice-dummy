package com.nextuple.sourcing.rule.cache.domain;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ServiceOptionInfo {
  private int priority;
  private Set<String> sourceNodes;
}
