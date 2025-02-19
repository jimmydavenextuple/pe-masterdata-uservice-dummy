package com.nextuple.jobs.consumers.authentication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

class CustomAuthConditionTest {
  @InjectMocks CustomAuthCondition condition;
  @Mock ConditionContext conditionContext;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Auth type is other than AWS Cognito")
  void matchesPositiveTest() {
    AnnotatedTypeMetadata mockMetadata = mock(AnnotatedTypeMetadata.class);
    Environment mockEnvironment = mock(Environment.class);
    when(conditionContext.getEnvironment()).thenReturn(mockEnvironment);
    when(mockEnvironment.getProperty("auth.type")).thenReturn("OtherType");
    // Act
    boolean result = condition.matches(conditionContext, mockMetadata);
    // Assert
    assertTrue(result);
  }

  @Test
  @DisplayName("Auth type is AWS Cognito")
  void matchesNegativeTest() {
    AnnotatedTypeMetadata mockMetadata = mock(AnnotatedTypeMetadata.class);
    Environment mockEnvironment = mock(Environment.class);
    when(conditionContext.getEnvironment()).thenReturn(mockEnvironment);
    when(mockEnvironment.getProperty("auth.type")).thenReturn("AWSCognito");
    // Act
    boolean result = condition.matches(conditionContext, mockMetadata);
    // Assert
    assertFalse(result);
  }
}
