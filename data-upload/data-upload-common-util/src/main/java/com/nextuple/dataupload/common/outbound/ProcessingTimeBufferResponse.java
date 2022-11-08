package com.nextuple.dataupload.common.outbound;

import com.nextuple.dataupload.common.pojo.ProcessingTimeBuffer;
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
public class ProcessingTimeBufferResponse implements Serializable {
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
