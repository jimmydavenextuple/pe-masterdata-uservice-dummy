package com.nextuple.promise.sourcing.rule.domain.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.promise.sourcing.rule.domain.primarykeys.PromiseSourcingRulePK;
import com.vladmihalcea.hibernate.type.json.JsonType;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@IdClass(PromiseSourcingRulePK.class)
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "promise_sourcing_rule",
    indexes =
        @Index(
            name = "orgId_serviceOption_destinationGeoZone_allocationRuleId",
            columnList = "orgId,serviceOption,destinationGeoZone,allocationRuleId"))
@TypeDef(name = "json", typeClass = JsonType.class)
@EntityListeners(CommonEntityListener.class)
public class PromiseSourcingRule {
  @Id private String orgId;

  @Id private String serviceOption;

  @Id private String destinationGeoZone;

  @Type(type = "json")
  @Column(name = "sourceNodes", columnDefinition = "json")
  private Set<String> sourceNodes;

  @Id private int priority;

  @Id private String allocationRuleId;
}
