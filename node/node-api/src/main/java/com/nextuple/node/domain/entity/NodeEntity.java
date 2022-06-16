package com.nextuple.node.domain.entity;

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
@IdClass(NodeId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "node")
public class NodeEntity {

  @Id
  @Column(name = "node_id")
  private String nodeId;

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Column(name = "street")
  private String street;

  @Column(name = "city")
  private String city;

  @Column(name = "province")
  private String province;

  @Column(name = "postal_code")
  private String postalCode;

  @Column(name = "country")
  private String country;

  @Column(name = "latitude")
  private String latitude;

  @Column(name = "longitude")
  private String longitude;

  @Column(name = "timezone")
  private String timezone;

  @Column(name = "ship_to_home")
  private Boolean shipToHome;

  @Column(name = "sdnd_eligible")
  private Boolean sdndEligible;

  @Column(name = "bopis_eligilble")
  private Boolean bopisEligible;

  @Column(name = "express_eligible")
  private Boolean expressEligible;

  @Column(name = "node_type")
  private String nodeType;

  @Column(name = "is_active")
  private Boolean isActive;
}
