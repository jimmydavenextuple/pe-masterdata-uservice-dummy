package com.hbc.calendar.domain.inbound;

import java.io.Serializable;
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
public class NodeCalendarRequest implements Serializable {

  @NotBlank(message = "calendarId can't be blank")
  @Length(max = 40)
  private String calendarId;

  @NotBlank(message = "nodeId can't be blank")
  @Length(max = 40)
  private String nodeId;

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 40)
  private String orgId;

  @NotBlank(message = "effectiveDate can't be blank")
  private String effectiveDate;

  private String description;
}
