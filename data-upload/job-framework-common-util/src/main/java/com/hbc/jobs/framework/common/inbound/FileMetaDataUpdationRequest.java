package com.hbc.jobs.framework.common.inbound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FileMetaDataUpdationRequest implements Serializable {

  private static final long serialVersionUID = -2149282044801518838L;

  private String name;

  private String path;

  private String size;

  private String type;

  private String storageType;

  private String description;

  private String extReferenceId;

  private Long parentFileId;

  private String updatedBy;
}
