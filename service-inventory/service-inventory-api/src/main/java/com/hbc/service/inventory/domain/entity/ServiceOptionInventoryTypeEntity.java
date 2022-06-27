package com.hbc.service.inventory.domain.entity;

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
@IdClass(ServiceToInventoryId.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "serviceOption_inventoryType")
public class ServiceOptionInventoryTypeEntity {
  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "service_option")
  private String serviceOption;

  @Column(name = "inventory_type")
  private String inventoryType;
}
