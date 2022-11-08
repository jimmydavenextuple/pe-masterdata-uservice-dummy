package com.nextuple.common.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class CommonBaseEntity extends BaseEntity {

  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;
}
