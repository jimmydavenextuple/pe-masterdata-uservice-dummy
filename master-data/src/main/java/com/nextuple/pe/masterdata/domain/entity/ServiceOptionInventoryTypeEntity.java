package com.nextuple.pe.masterdata.domain.entity;

import com.nextuple.pe.masterdata.domain.primaryKeys.ServiceToInventoryId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
