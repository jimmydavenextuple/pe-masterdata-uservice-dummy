package com.nextuple.postal.code.timezone.domain.repository;

import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  @Modifying
  @Query(
      value =
          "SELECT "
              + " v.country AS country , COUNT (DISTINCT v.city) AS noOfCities , COUNT (DISTINCT v.state) AS noOfStates "
              + " , COUNT (DISTINCT v.postal_code_prefix) AS noOfPostalCodePrefixes , "
              + " CAST( MAX( v.last_modified_date ) AS Date ) AS uploadDate FROM postal_code_timezone v WHERE v.org_id = ?1 "
              + "GROUP BY v.country ",
      nativeQuery = true)
  List<MarketRegionProjection> findRecordsByOrgId(String orgId);

  @Query(
      value =
          "SELECT postal_code_prefix FROM postal_code_timezone WHERE org_id = ?1 AND state = ?2",
      nativeQuery = true)
  List<String> findPostalCodePrefixListByOrgIdAndState(String orgId, String state);

  @Query(
      value = "SELECT * FROM postal_code_timezone WHERE org_id = ?1 AND country = ?2",
      nativeQuery = true)
  List<PostalCodeTimezoneEntity> findByOrgIdAndCountry(String orgId, String country);
}
