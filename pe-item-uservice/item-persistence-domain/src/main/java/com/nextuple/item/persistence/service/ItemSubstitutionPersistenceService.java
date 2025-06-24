/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.service;

import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import com.nextuple.item.persistence.domain.key.ItemSubstitutionDomainKey;
import java.util.List;
import java.util.Optional;

public interface ItemSubstitutionPersistenceService
    extends DomainPersistenceService<ItemSubstitutionDomainDto, ItemSubstitutionDomainKey> {
  List<ItemSubstitutionDomainDto> findByOrgIdAndPrimaryItemIdAndPrimaryUom(
      String orgId, String primaryItemId, String primaryUom);

  Optional<ItemSubstitutionDomainDto>
      findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
          String orgId,
          String primaryItemId,
          String primaryUom,
          String alternateItemId,
          String alternateUom);
}
