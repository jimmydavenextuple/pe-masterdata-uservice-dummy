package com.nextuple.postal.code.timezone.domain.repository;

import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostalCodeTimezoneRepository
    extends JpaRepository<PostalCodeTimezoneEntity, String> {
  PostalCodeTimezoneEntity save(PostalCodeTimezoneEntity postalCodeTimezoneEntity);

  PostalCodeTimezoneEntity findByOrgIdAndPostalCodePrefix(String orgId, String postalCodePrefix);
}
