package com.hbc.csvdownload.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadNodeCarrierServiceAndServiceOptionPojo {

  private Long contentsLength;
  private byte[] fileContents;
}
