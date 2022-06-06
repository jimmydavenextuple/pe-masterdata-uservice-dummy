package com.nextuple.common.cache.redis.config.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * A condition object to check whether Redisson should be enabled for dependent service or not.
 * Service supposed to enable the Redisson by setting spring.application.redisson.enabled to true.
 * With any other value, Redisson won't kick in.
 */
public class RedissonRequiredCondition extends AnyNestedCondition {

  public RedissonRequiredCondition() {
    super(ConfigurationPhase.PARSE_CONFIGURATION);
  }

  public RedissonRequiredCondition(ConfigurationPhase configurationPhase) {
    super(configurationPhase);
  }

  @ConditionalOnProperty(name = "spring.application.redisson.enabled", havingValue = "true")
  static class BasedOnTypeProperty {}
}
