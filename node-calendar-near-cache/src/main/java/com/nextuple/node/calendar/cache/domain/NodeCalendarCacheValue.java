package com.nextuple.node.calendar.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NodeCalendarCacheValue implements CacheValue {
  private static final long serialVersionUID = 2705609994396335116L;
  private List<NodeCalendarDaysStatusInfo> calendarDaysStatusInfo;
}
