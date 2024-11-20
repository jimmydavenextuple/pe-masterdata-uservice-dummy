package com.nextuple.pe.userexit.repository;

import com.nextuple.common.userexit.domain.entity.UserExitConfigData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExitConfigDataRepository extends JpaRepository<UserExitConfigData, String> {
  Optional<UserExitConfigData> findByUserExitNameAndAppNameAndOrgIdAndServiceName(
      String userExitName, String appName, String orgId, String serviceName);
}
