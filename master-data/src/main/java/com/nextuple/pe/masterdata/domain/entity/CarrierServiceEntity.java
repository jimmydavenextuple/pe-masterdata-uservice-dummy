package com.nextuple.pe.masterdata.domain.entity;

import com.nextuple.pe.masterdata.domain.primaryKeys.CarrierServiceId;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(CarrierServiceId.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "carrier_service")
@Builder
public class CarrierServiceEntity {

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "carrier_id")
  private String carrierId;

  @Id
  @Column(name = "service_id")
  private String serviceId;

  @Column(name = "carrier_name")
  private String carrierName;

  @Column(name = "service_name")
  private String serviceName;

  @Column(name = "service_options")
  private String serviceOptions;
}
