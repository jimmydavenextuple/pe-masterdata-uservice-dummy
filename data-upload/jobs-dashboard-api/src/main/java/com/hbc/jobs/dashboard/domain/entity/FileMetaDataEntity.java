package com.hbc.jobs.dashboard.domain.entity;

import com.hbc.common.base.CommonBaseEntity;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "file_metadata")
@SuperBuilder
public class FileMetaDataEntity extends CommonBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "path")
  private String path;

  @Column(name = "size")
  private String size;

  @Column(name = "storage_type")
  private String storageType;

  @Column(name = "type")
  private String type;

  @Column(name = "description")
  private String description;

  @Column(name = "ext_reference_id")
  private String extReferenceId;

  @Column(name = "parent_file_id")
  private String parentFileId;
}
