/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postalcodecarrierservice.persistence.repository;

import com.nextuple.postalcodecarrierservice.persistence.entity.PostalCodeCarrierServiceEntity;
import com.nextuple.postalcodecarrierservice.persistence.entity.key.PostalCodeCarrierServiceKey;
import com.nextuple.postgres.repository.CommonJpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostalCodeCarrierServiceRepository
    extends CommonJpaRepository<PostalCodeCarrierServiceEntity, PostalCodeCarrierServiceKey> {

  List<PostalCodeCarrierServiceEntity> findByZipcode(String zipcode);

  List<PostalCodeCarrierServiceEntity> findByCarrierServiceId(String carrierServiceId);

  @Query(
      value =
          "SELECT * FROM postal_code_carrier_service WHERE zipcode = ?1 AND carrier_service_id = ?2",
      nativeQuery = true)
  PostalCodeCarrierServiceEntity findByZipcodeAndCarrierServiceId(
      String zipcode, String carrierServiceId);

  @Query(
      value = "SELECT COUNT(*) FROM postal_code_carrier_service WHERE zipcode = ?1",
      nativeQuery = true)
  Long countByZipcode(String zipcode);
}
