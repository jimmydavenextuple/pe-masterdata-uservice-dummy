package com.nextuple.pe.masterdata.calendar.domain.inbound;

import com.nextuple.pe.masterdata.calendar.domain.pojo.ExceptionDays;
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

  @NotBlank(message = "calendarId cannot be blank")
  @Length(max = 40)
  private String calendarId;

  @NotBlank(message = "orgId cannot be blank")
  @Length(max = 40)
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
