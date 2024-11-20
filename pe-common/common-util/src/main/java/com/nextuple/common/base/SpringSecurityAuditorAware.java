package com.nextuple.common.base;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class SpringSecurityAuditorAware implements AuditorAware<String> {
  @Bean
  @Override
  public Optional<String> getCurrentAuditor() {

    return Optional.of("NEXTUPLE_GR");
  }
}
