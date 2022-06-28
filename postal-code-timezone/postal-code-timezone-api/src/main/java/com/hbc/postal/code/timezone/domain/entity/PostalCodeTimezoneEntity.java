package com.hbc.postal.code.timezone.domain.entity;

import com.hbc.core.event.listeners.CommonEntityListener;
import com.hbc.postal.code.timezone.domain.primaryKeys.PostalCodeTimezoneEntityPK;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(PostalCodeTimezoneEntityPK.class)
@EntityListeners(CommonEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@Table(name = "postal_code_timezone")
public class PostalCodeTimezoneEntity {
  @Id private String orgId;
  @Id private String postalCodePrefix;
  private String country;
  private String state;
  private String city;
  private String latitude;
  private String longitude;
  private String timeZone;
}
