package com.nextuple.csvdownload.common.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericUploadRequest {

  private String orgId;
  private String filePath;
  private String storageType;
  private String createdBy;
}
