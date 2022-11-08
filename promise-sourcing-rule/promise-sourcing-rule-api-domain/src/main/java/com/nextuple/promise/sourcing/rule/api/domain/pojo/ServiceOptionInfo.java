package com.nextuple.promise.sourcing.rule.api.domain.pojo;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ServiceOptionInfo implements Serializable {
  private static final long serialVersionUID = 6125425311438534564L;
  private int priority;
  private Set<String> sourceNodes;
}
