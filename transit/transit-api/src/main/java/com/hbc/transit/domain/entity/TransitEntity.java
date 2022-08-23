package com.hbc.transit.domain.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
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

  @Column(name = "buffer_days")
  private Double bufferDays;

  @Column(name = "bufferStartDate")
  private Date bufferStartDate;

  @Column(name = "bufferEndDate")
  private Date bufferEndDate;
}
