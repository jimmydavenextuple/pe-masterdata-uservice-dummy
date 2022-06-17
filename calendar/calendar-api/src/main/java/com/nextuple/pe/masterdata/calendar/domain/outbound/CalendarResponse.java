package com.nextuple.pe.masterdata.calendar.domain.outbound;

import com.nextuple.pe.masterdata.calendar.domain.pojo.ExceptionDays;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponse implements Serializable {

  private String calendarId;
  private String orgId;
  private String description;
  private Boolean isMondayWorking;
  private Boolean isTuesdayWorking;
  private Boolean isWednesdayWorking;
  private Boolean isThursdayWorking;
  private Boolean isFridayWorking;
  private Boolean isSaturdayWorking;
  private Boolean isSundayWorking;
  private List<ExceptionDays> exceptionDays;
}
