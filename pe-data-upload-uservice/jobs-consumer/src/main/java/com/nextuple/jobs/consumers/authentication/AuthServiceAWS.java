/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.authentication;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.jobs.consumers.feign.AuthTokenResponse;
import com.nextuple.jobs.consumers.service.AuthTokenService;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(value = "dataupload.type", havingValue = "S3")
public class AuthServiceAWS implements AuthService {

  private final AuthTokenService authTokenService;

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceAWS.class);

  @Override
  public void checkAuthExpiry(KafkaMessageHeaders headers) {
    long systime = System.currentTimeMillis();
    logger.debug("Auth token received in consumer");

    String authToken =
        authTokenService.getAuthToken(
            headers == null ? null : (String) headers.get(CommonConstants.AUTHORIZATION_HEADER),
            headers == null
                ? null
                : (String) headers.get(CommonConstants.AUTH_EXPIRY_TIMESTAMP_HEADER));

    logger.debug("Auth token took: {}", System.currentTimeMillis() - systime);
    CurrentThreadContext.getLogContext().setAuthorizationHeader(authToken);
  }

  @Override
  public void setAuthTokenInThreadContext() {
    long systime = System.currentTimeMillis();
    logger.debug("Auth token received in consumer");

    String authToken = authTokenService.generateAuthToken().getAccessToken();

    logger.debug("Auth token took: {}", System.currentTimeMillis() - systime);
    CurrentThreadContext.getLogContext().setAuthorizationHeader(authToken);
  }

  @Override
  public Message<RecordDto> setAuthHeaders(Message<RecordDto> message) {
    Message<RecordDto> messageWithHeaders;
    AuthTokenResponse authToken = authTokenService.generateAuthToken();
    LocalDateTime expiryTs = getAuthExpirationTime(authToken);
    messageWithHeaders =
        MessageBuilder.fromMessage(message)
            .setHeader(CommonConstants.AUTHORIZATION_HEADER, authToken.getAccessToken())
            .setHeader(
                CommonConstants.AUTH_EXPIRY_TIMESTAMP_HEADER,
                expiryTs.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    return messageWithHeaders;
  }

  private LocalDateTime getAuthExpirationTime(AuthTokenResponse authToken) {
    var dateTime = LocalDateTime.now();
    dateTime = dateTime.plusSeconds(authToken.getExpiresIn());
    return dateTime;
  }
}
