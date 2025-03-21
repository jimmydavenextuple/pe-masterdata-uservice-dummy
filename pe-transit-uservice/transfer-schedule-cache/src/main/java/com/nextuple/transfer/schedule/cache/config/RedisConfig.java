/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transfer.schedule.cache.config;

import com.nextuple.common.util.ObjectUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "redis-enabled", havingValue = "true")
public class RedisConfig {

  @Autowired RedisProperties redisProperties;

  @Value("${spring.data.redis.retry-attempt:3}")
  private int retryCount;

  @Value("${spring.data.redis.retry-interval:1500}")
  private int retryInterval;

  @Bean("redissonClient")
  public RedissonClient redissonClient() {
    Config config = new Config();
    String[] addresses = new String[redisProperties.getCluster().getNodes().size()];
    addresses =
        redisProperties.getCluster().getNodes().stream()
            .map(x -> "redis://" + x)
            .toList()
            .toArray(addresses);
    ClusterServersConfig clusterServersConfig = config.useClusterServers();
    clusterServersConfig.addNodeAddress(addresses);
    if (!ObjectUtil.isEmptyOrVoidString(redisProperties.getPassword())) {
      clusterServersConfig.setPassword(redisProperties.getPassword());
    }
    clusterServersConfig.setRetryAttempts(retryCount);
    clusterServersConfig.setRetryInterval(retryInterval);
    return Redisson.create(config);
  }
}
