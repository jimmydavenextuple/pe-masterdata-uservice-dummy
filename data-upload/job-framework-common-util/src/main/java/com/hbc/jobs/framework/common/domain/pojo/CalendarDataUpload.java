package com.hbc.jobs.framework.common.domain.pojo;

import com.hbc.calendar.domain.pojo.ExceptionDays;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarDataUpload {

  private String action;
  private String orgId;
  private String calendarId;
  private String description;
  private Boolean isFridayWorking;
  private Boolean isMondayWorking;
  private Boolean isTuesdayWorking;
  private Boolean isSundayWorking;
  private Boolean isWednesdayWorking;
  private Boolean isSaturdayWorking;
  private Boolean isThursdayWorking;
  private List<@Valid ExceptionDays> exceptionDays;
}
