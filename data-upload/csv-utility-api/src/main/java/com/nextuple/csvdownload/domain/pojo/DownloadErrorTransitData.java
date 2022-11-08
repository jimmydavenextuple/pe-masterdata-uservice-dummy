package com.nextuple.csvdownload.domain.pojo;

import com.nextuple.csvdownload.common.pojo.TransitDataUpload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadErrorTransitData extends TransitDataUpload {

  private String bufferDays;

  private String bufferStartDate;

  private String bufferEndDate;
}
