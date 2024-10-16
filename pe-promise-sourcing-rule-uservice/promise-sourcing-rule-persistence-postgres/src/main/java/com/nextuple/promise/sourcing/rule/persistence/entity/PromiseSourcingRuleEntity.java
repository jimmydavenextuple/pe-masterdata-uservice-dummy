/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postgres.entity.CommonBaseEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.PromiseSourcingRuleKey;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@IdClass(PromiseSourcingRuleKey.class)
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(
    name = "promise_sourcing_rule",
    indexes =
        @Index(
            name = "orgId_serviceOption_destinationGeoZone_allocationRuleId",
            columnList = "orgId,serviceOption,destinationGeoZone,allocationRuleId"))
public class PromiseSourcingRuleEntity extends CommonBaseEntity {
  @Id private String orgId;

  @Id private String serviceOption;

  @Id private String destinationGeoZone;

  @Type(JsonBinaryType.class)
  @Column(name = "sourceNodes", columnDefinition = "json")
  private Set<String> sourceNodes;

  @Id private int priority;
  @Id private String allocationRuleId;
}
