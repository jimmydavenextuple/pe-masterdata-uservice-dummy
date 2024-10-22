/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.common.userexit.domain.entity;

import com.nextuple.common.base.CommonBaseEntity;
import com.nextuple.common.userexit.domain.enums.ExecutionFailureEnum;
import com.nextuple.common.userexit.domain.enums.UserExitTypeEnum;
import com.nextuple.core.event.listeners.CommonEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(
    name = "user_exit_metadata",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "app_name", "service_name"})})
@EntityListeners(CommonEntityListener.class)
public class UserExitMetaData extends CommonBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "app_name")
  private String appName;

  @Column(name = "service_name")
  private String serviceName;

  @Column(name = "description")
  private String description;

  @Column(name = "execution_failure_type")
  private ExecutionFailureEnum executionFailureType;

  @Column(name = "type")
  private UserExitTypeEnum type;

  @Column(name = "pre_ue_Name")
  private String preUEName;

  @Column(name = "post_ue_Name")
  private String postUEName;
}
