package com.nextuple.jobs.consumers.authentication;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CustomAuthCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String authType = context.getEnvironment().getProperty("auth.type");
    return !"AWSCognito".equals(authType);
  }
}
