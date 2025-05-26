package com.nextuple.pe.light.promise.outbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InboundProcessingResponse {

    private String nodeId;

    private String orgId;

    private String startWorkingTime;

    private String lastWorkingTime;

    private List<Object> calendarDays;

    private Object inbound;

}
