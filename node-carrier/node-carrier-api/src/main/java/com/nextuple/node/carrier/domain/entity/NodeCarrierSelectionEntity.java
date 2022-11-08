package com.nextuple.node.carrier.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(NodeCarrierSelectionId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "node_carrier_selection")
public class NodeCarrierSelectionEntity {
  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "service_option")
  private String serviceOption;

  @Id
  @Column(name = "source_geozone")
  private String sourceGeozone;

  @Id
  @Column(name = "destination_geozone")
  private String destinationGeozone;

  private String priority;
}
