package com.nextuple.jobs.framework.common.domain.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordStatusDto implements Serializable {

  private static final long serialVersionUID = 7235171183817789960L;
  private String jobId;
  private String orgId;
  private Integer recordNo;
  private Integer statusCode;
  private ApiStatusEnum status;
  private String requestBody;
  private String responseBody;
  private String errorMessage;
  private String userId;
  private JobTypeEnum jobType;
  private Long responseTime;
  private Integer totalRecordsInJob;
  private JobReferences jobReferences;

  private String correlationId;
  private String serviceCorrelationId;
  private Boolean responseBodyPresent;
  private String exception = "None";

  public void setJobReferences(Map<String, Object> references) {
    this.jobReferences = new JobReferences(references);
  }
}
