/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transfer.schedule.cache.service.impl;

import com.nextuple.transfer.schedule.cache.repository.TransferScheduleRedisRepository;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferScheduleCacheServiceImpl {
  @Autowired TransferScheduleRedisRepository transferScheduleRedisRepository;

  public void saveTransferScheduleToRedis(TransferScheduleCreationRequest request) {
    transferScheduleRedisRepository.save(request);
  }
}
