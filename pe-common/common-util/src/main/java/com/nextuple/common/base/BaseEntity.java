package com.nextuple.common.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
public abstract class BaseEntity {

  @CreatedDate
  @Column(name = "created_date", updatable = false)
  @Builder.Default
  private Date createdDate = new Date();

  @LastModifiedDate
  @Column(name = "last_modified_date")
  @Builder.Default
  private Date lastModifiedDate = new Date();
}
