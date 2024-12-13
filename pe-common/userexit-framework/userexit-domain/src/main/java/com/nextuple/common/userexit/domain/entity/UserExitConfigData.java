/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.common.userexit.domain.entity;

import com.nextuple.common.base.CommonBaseEntity;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
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
    name = "user_exit_config",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"user_exit_name", "app_name", "org_id", "service_name"})
    })
@EntityListeners(CommonEntityListener.class)
public class UserExitConfigData extends CommonBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_exit_name")
  private String userExitName;

  @Column(name = "app_name")
  private String appName;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "service_name")
  private String serviceName;

  @Column(name = "url")
  private String url;

  @Column(name = "ue_impl_type")
  private UEImplTypeEnum ueImplType;

  @Column(name = "attribute_json_path")
  private String attributeJsonPath;

  @Column(name = "propagate_error")
  private Boolean propagateError;
}
