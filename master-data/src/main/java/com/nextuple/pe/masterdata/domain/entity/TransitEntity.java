package com.nextuple.pe.masterdata.domain.entity;

import com.nextuple.pe.masterdata.domain.primaryKeys.TransitId;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(TransitId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "transit_data")
public class TransitEntity {

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "source_geozone")
  private String sourceGeozone;

  @Id
  @Column(name = "destination_geozone")
  private String destinationGeozone;

  @Id
  @Column(name = "carrier_service_id")
  private String carrierServiceId;

  @Column(name = "transit_days")
  private Float transitDays;
}
