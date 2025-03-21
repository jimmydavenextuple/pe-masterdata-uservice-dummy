package com.nextuple.transit.domain.outbound;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferScheduleResponseWithType {
    @Schema(description = "Unique identifier of the transfer schedule record.", example = "1")
    List<String> requiredSourcingAttributes;
    @Schema(description = "Unique identifier of the transfer schedule record.", example = "1")
    List<String> optionalSourcingAttributes;
    @Schema(description = "Unique identifier of the transfer schedule record.", example = "1")
    List<TransferScheduleResponse> transferScheduleResponses;
}
