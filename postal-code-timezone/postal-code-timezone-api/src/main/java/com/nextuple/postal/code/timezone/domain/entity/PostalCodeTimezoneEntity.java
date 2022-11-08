package com.nextuple.postal.code.timezone.domain.entity;

import com.nextuple.common.base.BaseEntity;
import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postal.code.timezone.domain.primarykeys.PostalCodeTimezoneEntityPK;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(PostalCodeTimezoneEntityPK.class)
@EntityListeners(CommonEntityListener.class)
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name = "postal_code_timezone")
public class PostalCodeTimezoneEntity extends BaseEntity {
  @Id private String orgId;
  @Id private String postalCodePrefix;
  private String country;
  private String state;
  private String city;
  private String latitude;
  private String longitude;
  private String timeZone;
  private String createdBy;
  private String updatedBy;
}
