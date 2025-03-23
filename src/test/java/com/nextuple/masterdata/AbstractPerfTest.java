package com.nextuple.masterdata;

import com.nextuple.AbstractSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract class for Performance Test
 *
 * <p>Any common config & method for Performance tests can be added here.
 */
public abstract class AbstractPerfTest extends AbstractSpringBootTest {

  @BeforeEach
  public void setup() {
    util.refreshAccessToken();
  }

  @AfterEach
  public void tearDown() {
    util.unsubscribeFromTopics();
  }
}
