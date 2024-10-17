/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.jobs.consumers.exception.InvalidDateException;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.nextuple.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.http.HttpStatus;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    builder = @Builder(disableBuilder = true))
public interface NodeCarrierRequestMapper {

  NodeCarrierRequest convertToNodeCarrierRequest(ProcessingLeadTimesRaw processingLeadTimesRaw);

  NodeCarrierRequest convertToNodeCarrierRequest(NodeCarrierUpload nodeCarrierUpload);

  NodeCarrierUpdateRequest convertToNodeCarrierUpdateRequest(NodeCarrierUpload nodeCarrierUpload);

  @Mapping(target = "bufferStartDate", ignore = true)
  @Mapping(target = "bufferEndDate", ignore = true)
  @Mapping(target = "bufferHours", ignore = true)
  NodeCarrierBufferRequest convertToNodeCarrierBufferRequest(
      ProcessingTimeBufferUpload processingTimeBufferUpload) throws CommonServiceException;

  @Mapping(target = "bufferStartDate", ignore = true)
  @Mapping(target = "bufferEndDate", ignore = true)
  NodeServiceOptionBufferDeleteRequest convertToNodeServiceOptionBufferDeleteRequest(
      ProcessingTimeBufferUpload processingTimeBufferUpload);

  @AfterMapping
  default void convertBufferDates(
      final ProcessingTimeBufferUpload source,
      @MappingTarget final NodeServiceOptionBufferDeleteRequest target) {
    target.setBufferStartDate(convertStringToBufferDate(source.getBufferStartDate()));
    target.setBufferEndDate(convertStringToBufferDate(source.getBufferEndDate()));
  }

  @AfterMapping
  default void convertBufferHours(
      ProcessingTimeBufferUpload source, @MappingTarget NodeCarrierBufferRequest target)
      throws CommonServiceException {
    try {
      if (source.getBufferHours() != null) {
        target.setBufferHours(Double.parseDouble(source.getBufferHours()));
      }
    } catch (NumberFormatException e) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          "bufferHours", FieldError.builder().rejectedValue(source.getBufferHours()).build());
      throw new CommonServiceException(
          "Invalid buffer hours", HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }
  }

  @AfterMapping
  default void convertBufferDates(
      ProcessingTimeBufferUpload source, @MappingTarget NodeCarrierBufferRequest target) {
    target.setBufferStartDate(convertStringToBufferDate(source.getBufferStartDate()));
    target.setBufferEndDate(convertStringToBufferDate(source.getBufferEndDate()));
  }

  private Date convertStringToBufferDate(String dateStr) {
    if (!org.springframework.util.StringUtils.hasLength(dateStr)) {
      throw new InvalidDateException("Buffer date is empty or null ", dateStr);
    }
    try {
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      format.setLenient(false);
      return format.parse(dateStr);
    } catch (ParseException e) {
      throw new InvalidDateException("Invalid Date or Invalid Dateformat ", dateStr);
    }
  }
}
