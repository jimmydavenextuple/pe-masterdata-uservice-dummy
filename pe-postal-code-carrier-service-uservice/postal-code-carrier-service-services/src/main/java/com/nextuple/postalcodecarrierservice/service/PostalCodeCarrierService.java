/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postalcodecarrierservice.service;

import com.nextuple.postalcodecarrierservice.domain.dto.PostalCodeCarrierServiceCacheKeyDto;
import com.nextuple.postalcodecarrierservice.domain.dto.PostalCodeCarrierServiceDto;
import com.nextuple.postalcodecarrierservice.domain.inbound.PostalCodeCarrierServiceRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostalCodeCarrierService {

  PostalCodeCarrierServiceDto createPostalCodeCarrierService(
      PostalCodeCarrierServiceRequest request);

  PostalCodeCarrierServiceDto getPostalCodeCarrierService(String zipcode, String carrierServiceId);

  PostalCodeCarrierServiceDto updatePostalCodeCarrierService(
      String zipcode, String carrierServiceId, PostalCodeCarrierServiceRequest request);

  void deletePostalCodeCarrierService(String zipcode, String carrierServiceId);

  Page<PostalCodeCarrierServiceDto> getPostalCodeCarrierServices(Pageable pageable);

  List<PostalCodeCarrierServiceDto> getPostalCodeCarrierServicesByZipcode(String zipcode);

  List<PostalCodeCarrierServiceDto> getPostalCodeCarrierServicesByCarrierServiceId(
      String carrierServiceId);

  List<PostalCodeCarrierServiceCacheKeyDto> getAllCacheKeys();

  boolean existsPostalCodeCarrierService(String zipcode, String carrierServiceId);
}
