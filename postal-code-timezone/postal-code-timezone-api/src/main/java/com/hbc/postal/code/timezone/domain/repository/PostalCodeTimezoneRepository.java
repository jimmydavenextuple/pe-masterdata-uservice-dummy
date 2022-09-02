package com.hbc.postal.code.timezone.domain.repository;

import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostalCodeTimezoneRepository
    extends JpaRepository<PostalCodeTimezoneEntity, String> {
  PostalCodeTimezoneEntity save(PostalCodeTimezoneEntity postalCodeTimezoneEntity);

  PostalCodeTimezoneEntity findByOrgIdAndPostalCodePrefix(String orgId, String postalCodePrefix);

  @Query(
      value =
          "SELECT * FROM postal_code_timezone WHERE org_id = ?1 ORDER BY state ASC, postal_code_prefix ASC",
      nativeQuery = true)
  List<PostalCodeTimezoneEntity> findByOrgId(String orgId);

  @Query(
      value =
          "SELECT postal_code_prefix FROM postal_code_timezone WHERE org_id = ?1 and state = ?2",
      nativeQuery = true)
  List<String> findPostalCodePrefixListByOrgIdAndState(String orgId, String state);
}
