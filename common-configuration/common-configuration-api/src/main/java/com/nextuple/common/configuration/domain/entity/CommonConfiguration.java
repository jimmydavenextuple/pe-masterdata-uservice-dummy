package com.nextuple.common.configuration.domain.entity;

import com.nextuple.common.configuration.domain.primarykeys.CommonConfigurationPK;
import com.nextuple.core.event.listeners.CommonEntityListener;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(CommonConfigurationPK.class)
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "common_configuration",
    indexes = @Index(name = "orgId_type_key", columnList = "orgId,type,key"))
@EntityListeners(CommonEntityListener.class)
public class CommonConfiguration {
  @Id private String orgId;
  @Id private String type;
  @Id private String key;
  private String value;
}
