package com.nextuple.csvdownload.domain.pojo;

import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.ACTION_TYPE;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.NODE_ID;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.ORG_ID;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.PROCESSING_TIME;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.SERVICE_OPTIONS;

import com.opencsv.bean.CsvBindByName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessingLeadTimesRaw {

  @CsvBindByName(column = NODE_ID)
  private String nodeId;

  @CsvBindByName(column = ORG_ID)
  private String orgId;

  @CsvBindByName(column = SERVICE_OPTIONS)
  private String serviceOption;

  @CsvBindByName(column = PROCESSING_TIME)
  private String processingTime;

  @CsvBindByName(column = ACTION_TYPE)
  @NotEmpty
  @NotBlank(message = "action can not be empty")
  private String actionType;

  private String carrierServiceId = "";

  public static String[] columnHeadersArray() {
    return new String[] {NODE_ID, ORG_ID, SERVICE_OPTIONS, PROCESSING_TIME, ACTION_TYPE};
  }
}
