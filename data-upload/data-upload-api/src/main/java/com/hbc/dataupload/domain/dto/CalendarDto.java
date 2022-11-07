package com.hbc.dataupload.domain.dto;

import com.hbc.calendar.domain.outbound.CalendarResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarDto extends CalendarResponse {

  private Boolean isActive;
}
