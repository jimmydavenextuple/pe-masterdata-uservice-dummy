/*
 *
 *  * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.common.userexit;

import com.nextuple.common.userexit.domain.enums.UserExitTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "userexit",
    name = {"enabled"},
    havingValue = "True",
    matchIfMissing = false)
public class UserExitFactory<T, G> {
  @Autowired RegularUEImpl<T, G> regularUE;
  @Autowired ApiUEImpl<T, G> apiUE;

  public IUserExit<T, G> getUE(UserExitTypeEnum userExitTypeEnum) {
    if (UserExitTypeEnum.REGULAR.equals(userExitTypeEnum)) return regularUE;
    if (UserExitTypeEnum.API.equals(userExitTypeEnum)) return apiUE;
    return null;
  }
}
