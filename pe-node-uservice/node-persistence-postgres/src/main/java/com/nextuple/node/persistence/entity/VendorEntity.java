package com.nextuple.node.persistence.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.node.persistence.entity.key.VendorKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@IdClass(VendorKey.class)
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(name = "vendor")
public class VendorEntity extends CommonBaseEntity {
  @Id
  @Column(name = "vendor_id")
  String vendorId;

  @Column(name = "org_id")
  String orgId;

  @Column(name = "vendor_name")
  String vendorName;

  @Column(name = "description")
  String description;

  @Column(name = "vendor_type")
  String vendorType;
}
