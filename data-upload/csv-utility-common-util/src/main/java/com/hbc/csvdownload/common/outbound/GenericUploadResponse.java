package com.hbc.csvdownload.common.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericUploadResponse {

  private String orgId;
  private Long fileMetaDataId;
}
