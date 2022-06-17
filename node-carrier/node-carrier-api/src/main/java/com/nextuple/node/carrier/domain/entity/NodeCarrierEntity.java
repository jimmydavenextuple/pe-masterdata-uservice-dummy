package com.nextuple.node.carrier.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(NodeCarrierId.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "node_carrier")
public class NodeCarrierEntity {

  @Id
  @Column(name = "node_id")
  private String nodeId;

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "carrier_service_id")
  private String carrierServiceId;

  @Id
  @Column(name = "service_option")
  private String serviceOption;

  @Column(name = "processing_time")
  private Double processingTime;

  @Column(name = "last_pickup_time")
  private String lastPickupTime;
}
