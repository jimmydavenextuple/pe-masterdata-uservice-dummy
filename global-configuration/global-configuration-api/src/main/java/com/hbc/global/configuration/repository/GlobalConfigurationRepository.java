package com.hbc.global.configuration.repository;

import com.hbc.global.configuration.domain.entity.GlobalConfiguration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration, String> {
  List<GlobalConfiguration> findByKeyInAndOrgIdAndType(
      List<String> keys, String orgId, String type);

  Optional<GlobalConfiguration> findByOrgIdAndTypeAndKey(String orgId, String type, String key);

  List<GlobalConfiguration> findByKey(String key);
}
