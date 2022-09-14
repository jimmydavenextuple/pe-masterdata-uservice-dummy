package com.hbc.csvdownload.domain.pojo;

import com.opencsv.bean.CsvBindByName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingLeadTimesRaw {

  private static final String NODE_ID = "nodeId";
  private static final String ORG_ID = "orgId";
  private static final String SERVICE_OPTION = "serviceOptions";
  private static final String PROCESSING_TIME = "processingTime (in hrs)";
  private static final String ACTION_TYPE = "action";

  @CsvBindByName(column = NODE_ID)
  private String nodeId;

  @CsvBindByName(column = ORG_ID)
  private String orgId;

  @CsvBindByName(column = SERVICE_OPTION)
  private String serviceOption;

  @CsvBindByName(column = PROCESSING_TIME)
  private String processingTime;

  @CsvBindByName(column = ACTION_TYPE)
  @NotEmpty
  @NotBlank(message = "action can not be empty")
  private String actionType;

  public static String[] columnHeadersArray() {
    return new String[] {NODE_ID, ORG_ID, SERVICE_OPTION, PROCESSING_TIME, ACTION_TYPE};
  }
}
