package com.hbc.dataupload.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeWorkingCalendarDto implements Serializable {

  private static final long serialVersionUID = 5055863613059504920L;

  private String effectiveDate;
  private String calendarId;
}
