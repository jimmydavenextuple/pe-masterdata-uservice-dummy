/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.domain;

import com.nextuple.common.domain.DomainBaseEntity;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TransitBufferConfigRequestDomainDto extends DomainBaseEntity implements Serializable {
  private static final long serialVersionUID = -2975053871614783667L;

  private Long id;
  private String orgId;
  private String carrierServiceId;
  private Double bufferDays;
  private Date startDate;
  private Date endDate;
  private TransitBufferConfigRequestStatusEnum status;
  private Long parentRequestId;
  private Long fileMetaDataId;
  private Long downloadFileMetaDataId;
}
