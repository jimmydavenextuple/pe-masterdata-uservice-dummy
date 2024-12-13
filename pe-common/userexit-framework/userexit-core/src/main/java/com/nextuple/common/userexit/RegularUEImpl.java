/*
 *
 *  * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.common.userexit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.ErrorWrapper;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "userexit",
    name = "enabled",
    havingValue = "True",
    matchIfMissing = false)
public class RegularUEImpl<T, G> implements IUserExit<T, G> {
  @Autowired HttpUtil<T, G> httpUtil;
  @Autowired MeterRegistry meterRegistry;

  public ErrorWrapper<G> invoke(
      T inputData,
      Map<String, Object> customAttributeMap,
      UserExitData userExitData,
      TypeReference<T> inputClazz,
      TypeReference<G> outputClazz)
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    var timer =
        Timer.builder("userexit." + userExitData.getUserExitMetaData().getName())
            .tag("orgId", userExitData.getUserExitConfigData().getOrgId())
            .publishPercentiles(0.99, 0.95)
            .publishPercentileHistogram()
            .register(meterRegistry);
    long startTime = System.currentTimeMillis();

    if (UEImplTypeEnum.CLASS_BASED.equals(userExitData.getUserExitConfigData().getUeImplType())) {
      return (ErrorWrapper<G>) ErrorWrapper.builder().data(inputData).build();
    }
    ErrorWrapper<G> g =
        httpUtil.makePOSTCall(userExitData, inputData, customAttributeMap, outputClazz);
    timer.record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
    return g;
  }
}
