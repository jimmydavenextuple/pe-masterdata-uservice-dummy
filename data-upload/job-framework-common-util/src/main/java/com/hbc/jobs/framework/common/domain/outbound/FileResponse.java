package com.hbc.jobs.framework.common.domain.outbound;

import java.io.InputStream;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponse {

  private String bucketName;

  private String filePath;

  private String fileName;

  private InputStream inputStream;

  private String contentType;

  private Long contentLength;

  private Date lastModifiedDate;
}
