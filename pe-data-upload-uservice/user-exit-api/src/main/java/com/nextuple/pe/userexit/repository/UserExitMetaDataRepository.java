package com.nextuple.pe.userexit.repository;

import com.nextuple.common.userexit.domain.entity.UserExitMetaData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExitMetaDataRepository extends JpaRepository<UserExitMetaData, String> {
  Optional<UserExitMetaData> findByNameAndAppNameAndServiceName(
      String name, String appName, String serviceName);
}
