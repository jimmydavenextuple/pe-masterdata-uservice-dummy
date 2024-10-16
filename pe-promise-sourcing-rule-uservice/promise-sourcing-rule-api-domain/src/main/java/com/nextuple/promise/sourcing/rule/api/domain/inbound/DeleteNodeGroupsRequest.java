package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DeleteNodeGroupsRequest implements Serializable {
  private static final long serialVersionUID = 4893291940330497709L;

  private List<Long> nodeGroupIds;
}
