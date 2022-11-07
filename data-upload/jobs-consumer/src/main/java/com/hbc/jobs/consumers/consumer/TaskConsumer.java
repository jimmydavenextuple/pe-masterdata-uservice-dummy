package com.hbc.jobs.consumers.consumer;

import com.hbc.common.constants.CommonConstants;
import com.hbc.common.context.CurrentThreadContext;
import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.service.AuthTokenService;
import com.hbc.jobs.consumers.service.JobConsumerService;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final JobConsumerService jobConsumerService;

  private final AuthTokenService authTokenService;

  private static final Logger logger = LoggerFactory.getLogger(TaskConsumer.class);

  public TaskConsumer(JobConsumerService jobConsumerService, AuthTokenService authTokenAPI) {
    this.jobConsumerService = jobConsumerService;
    this.authTokenService = authTokenAPI;
  }

  @KafkaHandler(isDefault = true)
  public void receiveRecordFromDashboardProducer(
      @Payload RecordDto recordDto, @Headers KafkaMessageHeaders headers) throws JobException {
    logger.debug("Inside receiveRecordFromDashboardProducer service");

    try {
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
      long executTime = System.currentTimeMillis();
      RecordStatusDto recordStatus = jobConsumerService.executeTask(recordDto);

      logger.debug(
          "Execute task took {} for jobId: {}",
          System.currentTimeMillis() - executTime,
          recordStatus.getJobId());

      long jobUpdTime = System.currentTimeMillis();
      jobConsumerService.updateJobStatus(recordStatus);

      logger.debug(
          "Update job status took {} for jobId: {}",
          System.currentTimeMillis() - jobUpdTime,
          recordStatus.getJobId());
      logger.debug(
          "Overall record consumption took: {} for jobId: {}",
          System.currentTimeMillis() - systime,
          recordStatus.getJobId());
      logger.debug("receiveRecordFromDashboardProducer service ends");

    } catch (Exception e) {
      log.error("Error while receiving the job record from the kafka producer", e);
      throw new JobException(
          "Exception while receiving the job record from the kafka producer",
          e,
          recordDto.getJobId(),
          null);
    }
  }
}
