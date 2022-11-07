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
public class FileMetaDataCreationRequest extends FileMetaDataUpdationRequest
    implements Serializable {

  private static final long serialVersionUID = -6511139758836845833L;

  private String createdBy;
}
