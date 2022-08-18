package com.hbc.global.configuration.domain.entity;

import com.hbc.core.event.listeners.CommonEntityListener;
import com.hbc.global.configuration.domain.primarykeys.GlobalConfigurationPK;
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
@IdClass(GlobalConfigurationPK.class)
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "global_configuration",
    indexes = @Index(name = "orgId_type_key", columnList = "orgId,type,key"))
@EntityListeners(CommonEntityListener.class)
public class GlobalConfiguration {
  @Id private String orgId;
  @Id private String type;
  @Id private String key;
  private String value;
}
