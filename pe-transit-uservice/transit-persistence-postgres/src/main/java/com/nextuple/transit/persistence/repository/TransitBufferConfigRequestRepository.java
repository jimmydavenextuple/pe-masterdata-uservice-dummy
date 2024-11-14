/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.transit.persistence.entity.TransitBufferConfigRequestEntity;
import com.nextuple.transit.persistence.entity.key.TransitBufferConfigRequestKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitBufferConfigRequestRepository
    extends CommonJpaRepository<TransitBufferConfigRequestEntity, TransitBufferConfigRequestKey> {
  Optional<TransitBufferConfigRequestEntity> findById(Long id);

  @Query(
      value =
          "SELECT * FROM transit_buffer_config_request t WHERE t.org_id = ?1 AND t.carrier_service_id = ?2  AND t.status NOT IN ?3 ",
      nativeQuery = true)
  List<TransitBufferConfigRequestEntity> findByOrgIdAndCarrierServiceId(
      String orgId, String carrierServiceId, List<String> status);
}
