/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.postgres.mapper;

import com.nextuple.common.domain.DomainBaseEntity;
import com.nextuple.common.domain.key.DomainKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import com.nextuple.postgres.entity.key.BaseEntityKey;
import java.util.List;

public interface DomainToEntityMapper<
    DE extends DomainBaseEntity,
    DK extends DomainKey,
    E extends CommonBaseEntity,
    K extends BaseEntityKey> {

  DE toDomain(E e);

  E toEntity(DE de);

  K toEntityKey(DK dk);

  List<DE> toDomain(List<E> e);

  List<E> toEntity(List<DE> de);
}
