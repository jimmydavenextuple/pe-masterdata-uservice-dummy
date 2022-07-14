package com.hbc.calendar.domain.inbound;

import com.hbc.calendar.domain.pojo.ExceptionDays;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarRequest implements Serializable {

  @NotBlank(message = "calendarId can't be blank")
  @Length(max = 40)
  private String calendarId;

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 40)
  private String orgId;

  private String description;
  private Boolean isFridayWorking;
  private Boolean isMondayWorking;
  private Boolean isTuesdayWorking;
  private Boolean isThursdayWorking;
  private Boolean isSaturdayWorking;
  private Boolean isSundayWorking;
  private Boolean isWednesdayWorking;
  private List<ExceptionDays> exceptionDays;
}
