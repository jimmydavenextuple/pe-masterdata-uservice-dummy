package com.nextuple.common.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.AuditorAware;

public class SpringSecurityAuditorAwareTest {

  @Mock private AuditorAware auditorAware;

  @InjectMocks private SpringSecurityAuditorAware springSecurityAuditorAware;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getCurrentAuditorTest() {
    when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("NEXTUPLE_GR"));
    Optional<String> result = springSecurityAuditorAware.getCurrentAuditor();
    assertNotNull(result);
    assertEquals(Optional.of("NEXTUPLE_GR"), result);
  }
}
