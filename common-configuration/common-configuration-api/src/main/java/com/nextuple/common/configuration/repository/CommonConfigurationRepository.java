package com.nextuple.common.configuration.repository;

import com.nextuple.common.configuration.domain.entity.CommonConfiguration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonConfigurationRepository extends JpaRepository<CommonConfiguration, String> {
  Optional<CommonConfiguration> findByOrgIdAndTypeAndKey(String orgId, String type, String key);
}
