package com.hbc.weightage.configuration.domain.repository;

import com.hbc.weightage.configuration.domain.entity.WeightageConfiguration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightageConfigurationRepository
    extends JpaRepository<WeightageConfiguration, String> {
  List<WeightageConfiguration> findByKeyInAndOrgIdAndType(
      List<String> keys, String orgId, String type);

  WeightageConfiguration findByOrgIdAndTypeAndKey(String orgId, String type, String key);

  List<WeightageConfiguration> findByKey(String key);
}
