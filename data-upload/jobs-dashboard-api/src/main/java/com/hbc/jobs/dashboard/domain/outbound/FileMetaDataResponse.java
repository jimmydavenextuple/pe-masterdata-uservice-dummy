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

  private String name;

  private String path;

  private String size;

  private String type;

  private String storageType;

  private String description;

  private String extReferenceId;

  private String parentFileId;
}
