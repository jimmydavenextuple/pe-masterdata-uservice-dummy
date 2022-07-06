package com.hbc.common.base;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {

  @Column(name = "created_date", updatable = false)
  private Date createdDate = new Date();

  @Column(name = "last_modified_date")
  private Date lastModifiedDate = new Date();
}
