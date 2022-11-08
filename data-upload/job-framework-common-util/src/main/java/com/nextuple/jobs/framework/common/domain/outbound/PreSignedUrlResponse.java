package com.nextuple.jobs.framework.common.domain.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreSignedUrlResponse {

  private String signedURL;
  private String filePath;
  private String storageType;
}
