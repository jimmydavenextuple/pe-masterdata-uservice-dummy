package com.hbc.jobs.consumers.consumer;

import com.hbc.common.context.CurrentThreadContext;
import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.feign.AuthTokenAPI;
import com.hbc.jobs.consumers.feign.AuthTokenRequest;
import com.hbc.jobs.consumers.feign.AuthTokenResponse;
import com.hbc.jobs.consumers.service.JobConsumerService;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@KafkaListener(
    topics = "${jobs-framework.record-consumer.topic-name}",
    groupId = "${jobs-framework.record-consumer.group-id}")
@Component
@Slf4j
public class TaskConsumer {

  @Value("${auth-token.grant-type:client_credentials}")
  public String grantType;

  @Value("${auth-token.scope:sfcc-resources/edd}")
  public String scope;

  private final JobConsumerService jobConsumerService;

  private final AuthTokenAPI authTokenAPI;

  public TaskConsumer(JobConsumerService jobConsumerService, AuthTokenAPI authTokenAPI) {
    this.jobConsumerService = jobConsumerService;
    this.authTokenAPI = authTokenAPI;
  }

  @KafkaHandler(isDefault = true)
  public void receiveRecordFromDashboardProducer(
      @Payload RecordDto recordDto, @Headers KafkaMessageHeaders headers) throws JobException {
    log.info("Inside receiveRecordFromDashboardProducer service");

    try {
      log.debug("Auth token received in consumer");
      String authToken = getAuthToken();
      CurrentThreadContext.getLogContext().setAuthorizationHeader(authToken);
      jobConsumerService.processRecord(recordDto);
      log.info("receiveRecordFromDashboardProducer service ends");
    } catch (Exception e) {
      log.error("Error while receiving the job record from the kafka producer", e);
      throw new JobException(
          "Exception while receiving the job record from the kafka producer",
          e,
          recordDto.getJob().getJobId(),
          null);
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
