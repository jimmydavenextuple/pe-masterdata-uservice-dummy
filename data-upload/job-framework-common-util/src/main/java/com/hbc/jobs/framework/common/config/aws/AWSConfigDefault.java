package com.hbc.jobs.framework.common.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(value = "storage.type", havingValue = "S3")
@Profile("default")
public class AWSConfigDefault {

  @Bean
  public AmazonS3 s3() {
    AWSCredentials awsCredentials = new BasicSessionCredentials("", "", "");
    return AmazonS3ClientBuilder.standard()
        .withRegion(String.valueOf(Region.getRegion(Regions.US_EAST_1)))
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .build();
  }
}
