/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.util;

import com.nextuple.common.util.JsonUtil;
import com.nextuple.csvdownload.exception.JsonParsingException;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtil {

  private StringUtil() {}

  public static String createJobRequest(List<?> requestList, String orgId)
      throws JsonParsingException {
    log.debug("Inside create job request method");
    String jobRequest;
    try {
      Function<List<?>, String> dataMapper = request -> JsonUtil.convert(requestList);
      jobRequest = dataMapper.apply(requestList);
    } catch (Exception e) {
      log.error("Error in parsing Job Json String", e);
      throw new JsonParsingException("Error in parsing Job Json String", e, orgId);
    }
    return jobRequest;
  }
}
