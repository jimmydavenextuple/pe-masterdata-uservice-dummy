package com.nextuple.node.domain.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@IdClass(NodePK.class)
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "node")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
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

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb", name = "service_option_eligibilities")
  private Map<String, Boolean> serviceOptionEligibilities;

  @Column(name = "ship_to_home")
  private Boolean shipToHome;

  @Column(name = "bopis_eligible")
  private Boolean bopisEligible;

  @Column(name = "node_type")
  private String nodeType;

  @Column(name = "is_active")
  private Boolean isActive;
}
