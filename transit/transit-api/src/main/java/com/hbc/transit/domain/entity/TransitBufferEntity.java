package com.hbc.transit.domain.entity;

import com.hbc.common.base.CommonBaseEntity;
import com.hbc.core.event.listeners.CommonEntityListener;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(
    name = "transit_buffer_data",
    indexes =
        @Index(
            name = "transitBufferConfigRequestId_key",
            columnList = "transit_buffer_config_request_id"))
@IdClass(TransitId.class)
@EntityListeners(CommonEntityListener.class)
public class TransitBufferEntity extends CommonBaseEntity {

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "carrier_service_id")
  private String carrierServiceId;

  @Id
  @Column(name = "source_geozone")
  private String sourceGeozone;

  @Id
  @Column(name = "destination_geozone")
  private String destinationGeozone;

  @Column(name = "buffer_days")
  private Double bufferDays;

  @Column(name = "buffer_start_date")
  private Date bufferStartDate;

  @Column(name = "buffer_end_date")
  private Date bufferEndDate;

  @Column(name = "transit_buffer_config_request_id")
  private Long transitBufferConfigRequestId;
}
