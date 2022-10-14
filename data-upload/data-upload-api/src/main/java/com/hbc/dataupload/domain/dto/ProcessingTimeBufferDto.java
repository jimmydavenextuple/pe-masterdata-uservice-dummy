package com.hbc.dataupload.domain.dto;

import com.hbc.dataupload.domain.pojo.ProcessingTimeBuffer;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessingTimeBufferDto implements Serializable {
  private static final long serialVersionUID = -1766230774577746452L;

  private String nodeId;
  private String orgId;
  private String nodeType;
  private String street;
  private String city;
  private String province;
  private String postalCode;
  private List<String> serviceOptions;
  private List<ProcessingTimeBuffer> processingTimeBuffers;
}
