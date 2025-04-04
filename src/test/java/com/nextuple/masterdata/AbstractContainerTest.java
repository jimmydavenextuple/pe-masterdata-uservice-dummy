package com.nextuple.masterdata;

import com.nextuple.AbstractSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract class for REST container tests.
 *
 * <p>Any common config & method for REST container tests can be added here.
 */
public abstract class AbstractContainerTest extends AbstractSpringBootTest {

  @BeforeEach
  public void setup() {
    util.refreshAccessToken();
  }

  @AfterEach
  public void tearDown() {
    util.unsubscribeFromTopics();
  }
}
