package com.nextuple.csvdownload.util;

import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeCalendarUtil {
  public static Optional<NodeCarrierServiceCalendarResponse> getActiveCalendarForNodeIdAndCarrier(
      List<NodeCarrierServiceCalendarResponse> calendars) {
    String presentDate = DataUploadUtil.getPresentDate();
    Optional<NodeCarrierServiceCalendarResponse> activeCalendar =
        calendars.stream()
            .filter(calendar -> calendar.getEffectiveDate().compareTo(presentDate) <= 0)
            .max(Comparator.comparing(NodeCarrierServiceCalendarResponse::getEffectiveDate));
    if (activeCalendar.isEmpty()) {
      activeCalendar =
          calendars.stream()
              .filter(calendar -> calendar.getEffectiveDate().compareTo(presentDate) > 0)
              .min(Comparator.comparing(NodeCarrierServiceCalendarResponse::getEffectiveDate));
    }
    return activeCalendar;
  }
}
