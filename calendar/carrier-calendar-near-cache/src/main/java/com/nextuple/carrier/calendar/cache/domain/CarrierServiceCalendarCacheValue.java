package com.nextuple.carrier.calendar.cache.domain;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.core.cache.domain.CacheValue;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CarrierServiceCalendarCacheValue implements CacheValue {
  private static final long serialVersionUID = 2705609994396335116L;
  private List<CalendarDaysStatusInfo> calendarDaysStatusInfo;

  @Override
  public boolean isUndefined() {
    return false;
  }
}
