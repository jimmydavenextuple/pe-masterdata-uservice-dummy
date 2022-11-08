package com.nextuple.weightage.configuration.domain.repository;

import com.nextuple.weightage.configuration.domain.entity.WeightageConfiguration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightageConfigurationRepository
    extends JpaRepository<WeightageConfiguration, String> {
  List<WeightageConfiguration> findByKeyInAndOrgIdAndType(
      List<String> keys, String orgId, String type);

  WeightageConfiguration findByOrgIdAndTypeAndKey(String orgId, String type, String key);

  List<WeightageConfiguration> findByKey(String key);

  @Query(value = "SELECT * FROM weightage_configuration LIMIT ?1", nativeQuery = true)
  List<WeightageConfiguration> findAllRecords(Integer limit);
}
