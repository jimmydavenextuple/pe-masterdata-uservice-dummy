package com.hbc.node.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeWorkingCalendarDto implements Serializable {

    private static final long serialVersionUID = 5055863613059504920L;

    private String effectiveDate;
    private String calendarId;
}
