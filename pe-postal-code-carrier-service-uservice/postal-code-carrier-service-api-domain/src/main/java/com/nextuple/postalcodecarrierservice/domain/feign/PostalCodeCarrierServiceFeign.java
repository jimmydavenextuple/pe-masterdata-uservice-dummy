/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postalcodecarrierservice.domain.feign;

import com.nextuple.postalcodecarrierservice.domain.inbound.PostalCodeCarrierServiceRequest;
import com.nextuple.postalcodecarrierservice.domain.outbound.PostalCodeCarrierServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-postalcode-carrier-uservice",
    url =
        "${spring.application.dependencies.postalcode-carrier:http://pe-postalcode-carrier-uservice:8080/}")
public interface PostalCodeCarrierServiceFeign {

  @PostMapping("/api/v1/postal-code-carrier-service")
  PostalCodeCarrierServiceResponse createPostalCodeCarrierService(
      @RequestBody PostalCodeCarrierServiceRequest request);

  @GetMapping("/api/v1/postal-code-carrier-service/{zipcode}/{carrierServiceId}")
  PostalCodeCarrierServiceResponse getPostalCodeCarrierService(
      @PathVariable String zipcode, @PathVariable String carrierServiceId);

  @PutMapping("/api/v1/postal-code-carrier-service/{zipcode}/{carrierServiceId}")
  PostalCodeCarrierServiceResponse updatePostalCodeCarrierService(
      @PathVariable String zipcode,
      @PathVariable String carrierServiceId,
      @RequestBody PostalCodeCarrierServiceRequest request);

  @DeleteMapping("/api/v1/postal-code-carrier-service/{zipcode}/{carrierServiceId}")
  PostalCodeCarrierServiceResponse deletePostalCodeCarrierService(
      @PathVariable String zipcode, @PathVariable String carrierServiceId);

  @GetMapping("/api/v1/postal-code-carrier-service")
  PostalCodeCarrierServiceResponse get(Pageable pageable);
}
