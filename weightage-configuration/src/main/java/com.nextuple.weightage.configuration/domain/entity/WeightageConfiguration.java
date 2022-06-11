package com.nextuple.weightage.configuration.domain.entity;

import com.nextuple.weightage.configuration.domain.primaryKeys.WeightageConfigurationPK;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(WeightageConfigurationPK.class)
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "weightage_configuration",
    indexes = @Index(name = "orgId_type_key", columnList = "orgId,type,key"))
public class WeightageConfiguration {
  @Id private String orgId;
  @Id private String type;
  @Id private String key;
  private Float weightage;
}
