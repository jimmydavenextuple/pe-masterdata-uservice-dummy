package com.nextuple.transit.consumer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TransferScheduleDto extends CommonMasterDataFieldsDto implements Serializable {

  @Serial private static final long serialVersionUID = 7078170315367233060L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Unique identifier of the source node of the transfer.", example = "Node1")
  private String sourceNodeId;

  @Schema(
      description = "Unique identifier of the drop off node of the transfer.",
      example = "Node2")
  private String dropoffNodeId;

  @Schema(
      description = "Start date and time of the transfer.",
      example = "2024-10-31T01:30:00.000-05:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", lenient = OptBoolean.FALSE)
  private DateTime startTime;

  @Schema(
      description = "End date and time of the transfer.",
      example = "2024-10-31T01:30:00.000-05:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", lenient = OptBoolean.FALSE)
  private DateTime endTime;

  @Schema(
      description =
          "Rule containing comma-separated values, associated with the transfer schedule.",
      example = "DC:KITCHEN")
  private String rule;

  @Schema(
      description = "Name of the rule associated with the transfer schedule.",
      example = "KitchenItemRule")
  private String ruleName;
}
