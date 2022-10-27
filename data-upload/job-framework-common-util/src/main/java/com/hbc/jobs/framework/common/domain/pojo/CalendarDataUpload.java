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
  private List<@Valid ExceptionDays> exceptionDays;
}
