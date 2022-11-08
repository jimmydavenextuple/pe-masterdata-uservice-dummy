package com.nextuple.common.base;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class BaseEntity {

  @Column(name = "created_date", updatable = false)
  @Builder.Default
  private Date createdDate = new Date();

  @Column(name = "last_modified_date")
  @Builder.Default
  private Date lastModifiedDate = new Date();
}
