package com.hbc.jobs.dashboard.domain.outbound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetaDataResponse implements Serializable {

  private long id;

  private String fileName;

  private String filePath;

  private String fileSize;

  private String fileType;

  private String storageType;

  private String description;

  private String extReferenceId;

  private String parentFileId;
}
