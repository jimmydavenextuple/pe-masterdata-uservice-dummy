package com.hbc.transit.domain.entity;

import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "transit_buffer_config_request")
public class TransitBufferConfigRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "carrier_service_id")
  private String carrierServiceId;

  @Column(name = "buffer_days")
  private Double bufferDays;

  @Column(name = "start_date")
  private Date startDate;

  @Column(name = "end_date")
  private Date endDate;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private TransitBufferConfigRequestStatusEnum status;

  @Column(name = "parent_request_id")
  private Long parentRequestId;
}
