package com.nextuple.node.carrier.domain.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(NodeCarrierId.class)
@EntityListeners(CommonEntityListener.class)
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

  @Column(name = "buffer_hours")
  private Double bufferHours;

  @Column(name = "buffer_start_date")
  private Date bufferStartDate;

  @Column(name = "buffer_end_date")
  private Date bufferEndDate;
}
