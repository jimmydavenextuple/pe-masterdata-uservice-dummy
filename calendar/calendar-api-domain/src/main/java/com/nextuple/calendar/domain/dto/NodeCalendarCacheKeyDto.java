package com.nextuple.calendar.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeCalendarCacheKeyDto implements Serializable {
  private static final long serialVersionUID = 8735290437193371305L;

  private String nodeId;
  private String orgId;
}
