package com.hbc.jobs.consumers.consumer;

import com.hbc.common.context.CurrentThreadContext;
import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.feign.AuthTokenAPI;
import com.hbc.jobs.consumers.feign.AuthTokenRequest;
import com.hbc.jobs.consumers.feign.AuthTokenResponse;
import com.hbc.jobs.consumers.service.JobConsumerService;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@KafkaListener(
    topics = "${jobs-framework.result-consumer.topic-name}",
    groupId = "${jobs-framework.result-consumer.group-id}")
@Component
@Slf4j
public class ResultConsumer {

  @Value("${auth-token.grant-type:client_credentials}")
  public String grantType;

  @Value("${auth-token.scope:sfcc-resources/edd}")
  public String scope;

  private final JobConsumerService jobConsumerService;

  private final AuthTokenAPI authTokenAPI;

  public ResultConsumer(JobConsumerService jobConsumerService, AuthTokenAPI authTokenAPI) {
    this.jobConsumerService = jobConsumerService;
    this.authTokenAPI = authTokenAPI;
  }

  @KafkaHandler(isDefault = true)
  public void receiveResultFromAnotherConsumer(
      @Payload RecordStatusDto recordStatus, @Headers KafkaMessageHeaders headers)
      throws JobException {
    log.debug("Inside receiveResultFromAnotherConsumer service");

    try {
      log.debug("Auth token received in consumer");
      String authToken = getAuthToken();
      CurrentThreadContext.getLogContext().setAuthorizationHeader(authToken);
      jobConsumerService.updateJobStatus(recordStatus);
    } catch (Exception e) {
      log.error("Error while receiving the job from kafka", e);
      throw new JobException("Exception while persisting the job record", e, null, null);
    }
  }

  private String getAuthToken() {
    var authTokenRequest = AuthTokenRequest.builder().grant_type(grantType).scope(scope).build();

    AuthTokenResponse response = authTokenAPI.getAuthToken(authTokenRequest);
    String authToken = response.getAccessToken();
    log.debug("Auth Token generated is : {}", authToken);
    return "Bearer " + authToken;
  }
}
